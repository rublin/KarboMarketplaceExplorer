package org.rublin.provider.livecoin;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;
import org.rublin.provider.Marketplace;
import org.rublin.provider.cryptopia.MarketOrders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component("livecoin")
public class LivecoinMarketplace implements Marketplace {

    public static final String LIVECOIN = "https://api.livecoin.net/exchange/order_book?currencyPair=";

    @Override
    public List<OptimalOrderDto> tradesByPair(PairDto pair) {
        RestTemplate template = new RestTemplate();
        Currency buy = pair.getBuyCurrency();
        Currency sell = pair.getSellCurrency();

        String url = LIVECOIN.concat("KRB/").concat(pair.isBought() ? sell.name() : buy.name());

        OrderBook livecoinResults = null;
        try {
            log.info("Send {} req", url);
            livecoinResults = template.getForObject(url, OrderBook.class);
        } catch (Throwable e) {
            log.warn("{} error", e.getMessage());
        }

        List<OptimalOrderDto> result = new ArrayList<>();
        if (livecoinResults != null) {
            log.info("{} returns {} buy and {} sell orders",
                    TradePlatform.LIVECOIN,
                    livecoinResults.getAsks().size(),
                    livecoinResults.getBids().size());

            if (pair.isBought()) {
                List<OptimalOrderDto> orders = livecoinResults.getAsks().stream()
                        .map(trade -> OptimalOrderDto.builder()
                                .marketplace(TradePlatform.LIVECOIN.name())
                                .amountToSale(trade[1].multiply(trade[0]))
                                .amountToBuy(trade[1])
                                .rate(trade[0])
                                .build())
                        .collect(toList());
                result.addAll(orders);
            } else {
                List<OptimalOrderDto> orders = livecoinResults.getBids().stream()
                        .map(trade -> OptimalOrderDto.builder()
                                .marketplace(TradePlatform.LIVECOIN.name())
                                .amountToSale(trade[1])
                                .amountToBuy(trade[1].multiply(trade[0]))
                                .rate(trade[0])
                                .build())
                        .collect(toList());
                result.addAll(orders);
            }
        }
        return result;
    }
}

