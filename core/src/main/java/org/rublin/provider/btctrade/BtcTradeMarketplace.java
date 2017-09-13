package org.rublin.provider.btctrade;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;
import org.rublin.provider.Marketplace;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component("btc")
public class BtcTradeMarketplace implements Marketplace {

    public static final String BTC_TRADE_URL = "https://btc-trade.com.ua/api/trades/";

    @Override
    public List<OptimalOrderDto> tradesByPair(PairDto pair) {
        RestTemplate template = new RestTemplate();
        Currency buy = pair.getBuyCurrency();
        Currency sell = pair.getSellCurrency();

        String url = BTC_TRADE_URL;
        if (sell == Currency.KRB) {
            url = url.concat("buy/").concat(sell.name()).concat("_").concat(buy.name());
        } else {
            url = url.concat("sell/").concat(buy.name()).concat("_").concat(sell.name());
        }
        TradesBuyPair btcTradeResult = null;
        try {
            log.info("Send {} req", url);
            btcTradeResult = template.getForObject(url, TradesBuyPair.class);
        } catch (RestClientException e) {
            log.warn("{} error", e.getMessage());
        }

        List<OptimalOrderDto> result = new ArrayList<>();
        if (btcTradeResult != null) {
            log.info("{} returns {} orders", TradePlatform.BTC_TRADE, btcTradeResult.getTrades().size());
            List<OptimalOrderDto> orders = btcTradeResult.getTrades().stream()
                    .map(trade -> OptimalOrderDto.builder()
                            .marketplace(TradePlatform.BTC_TRADE.name())
                            .amountToSale(trade.getCurrencyTrade())
                            .amountToBuy(trade.getCurrencyBase())
                            .rate(trade.getPrice())
                            .build())
                    .collect(toList());
            result.addAll(orders);
        }
        return result;
    }
}
