package org.rublin.provider.cryptopia;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;
import org.rublin.provider.Marketplace;
import org.rublin.provider.btctrade.TradesBuyPair;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component("ctyptopia")
public class CryptopiaMarketplace implements Marketplace {

    public static final String CRYPTOPIA_URL = "https://www.cryptopia.co.nz/api/GetMarketOrders";

    @Override
    public List<OptimalOrderDto> tradesByPair(PairDto pair) {
        RestTemplate template = new RestTemplate();
        Currency buy = pair.getBuyCurrency();
        Currency sell = pair.getSellCurrency();

        String url = CRYPTOPIA_URL.concat("/KRB_").concat(pair.isBought() ? sell.name() : buy.name());

        MarketOrders cryptopiaResult = null;
        try {
            log.info("Send {} req", url);
            cryptopiaResult = template.getForObject(url, MarketOrders.class);
        } catch (RestClientException e) {
            log.warn("{} error", e.getMessage());
        }

        List<OptimalOrderDto> result = new ArrayList<>();
        if (Objects.nonNull(cryptopiaResult) && cryptopiaResult.getSuccess() && Objects.nonNull(cryptopiaResult.getData())) {
            log.info("{} returns {} buy and {} sell orders",
                    TradePlatform.CRYPTOPIA,
                    cryptopiaResult.getData().getBuy().size(),
                    cryptopiaResult.getData().getSell().size());

            if (pair.isBought()) {
                List<OptimalOrderDto> orders = cryptopiaResult.getData().getSell().stream()
                        .map(trade -> OptimalOrderDto.builder()
                                .marketplace(TradePlatform.CRYPTOPIA.name())
                                .amountToSale(trade.getMTotal())
                                .amountToBuy(trade.getMVolume())
                                .rate(trade.getMPrice())
                                .build())
                        .collect(toList());
                result.addAll(orders);
            } else {
                List<OptimalOrderDto> orders = cryptopiaResult.getData().getBuy().stream()
                        .map(trade -> OptimalOrderDto.builder()
                                .marketplace(TradePlatform.CRYPTOPIA.name())
                                .amountToSale(trade.getMVolume())
                                .amountToBuy(trade.getMTotal())
                                .rate(trade.getMPrice())
                                .build())
                        .collect(toList());
                result.addAll(orders);
            }
        }
        return result;
    }
}
