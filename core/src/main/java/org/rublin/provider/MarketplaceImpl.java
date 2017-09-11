package org.rublin.provider;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.rublin.provider.btctrade.TradesBuyPair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
public class MarketplaceImpl implements Marketplace {
    public static final String BTC_TRADE_URL = "https://btc-trade.com.ua/api/trades/";
    public static final String CRYPTOPIA_URL = "https://www.cryptopia.co.nz/api/GetMarketOrders/KRB_BTC";
    public static final String LIVECOIN = "https://api.livecoin.net/exchange/order_book?currencyPair=KRB/BTC";

    @Override
    public Map<TradePlatform, List<OptimalOrderDto>> tradesByPair(PairDto pair) {
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

        Map<TradePlatform, List<OptimalOrderDto>> result = new HashMap<>();
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
            result.put(TradePlatform.BTC_TRADE, orders);
        }
//        log.debug("Success with {} results. Min price {}, Max price {}", result.getTrades().size(), result.getMinPrice(), result.getMaxPrice());
//        result.getTrades().forEach(order -> log.debug("Order amount {} with price {}", order.getCurrencyTrade(), order.getPrice()));
//
//        uri = "https://www.cryptopia.co.nz/api/GetMarketOrders/KRB_BTC";
//        MarketOrders cryptopiaResult = template.getForObject(uri, MarketOrders.class);
//
//        log.debug("{} from cryptopia with {} results", cryptopiaResult.getSuccess() ? "success" : "fail", cryptopiaResult.getData().getMBuy().size());
//
//        uri = "https://api.livecoin.net/exchange/order_book?currencyPair=KRB/BTC";
//         = null;
        return result;
    }

    /*private T requestOrders(String url, T cl) {
        RestTemplate template = new RestTemplate();
        try {
            OrderBook livecoinResult = template.getForObject(url, );
            log.debug("{) results from livecoin", livecoinResult.getAsks().size());
            livecoinResult.getAsks().forEach(ask -> log.debug("Asc {} ", ask));
        } catch (RestClientException e) {
            log.warn("Error from provider: {}", e.getMessage());
        }
        return null;
    }*/
}
