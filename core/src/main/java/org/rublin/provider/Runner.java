package org.rublin.provider;

import lombok.extern.slf4j.Slf4j;
import org.rublin.provider.livecoin.OrderBook;
import org.rublin.provider.cryptopia.MarketOrders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.rublin.provider.btctrade.TradesBuyPair;

@Slf4j
public class Runner {

    public static void main(String[] args) {

        String uri = "https://btc-trade.com.ua/api/trades/sell/krb_uah";
        RestTemplate template = new RestTemplate();
        TradesBuyPair result = template.getForObject(uri, TradesBuyPair.class);

        log.debug("Success with {} results. Min price {}, Max price {}", result.getTrades().size(), result.getMinPrice(), result.getMaxPrice());
        result.getTrades().forEach(order -> log.debug("Order amountSell {} with price {}", order.getCurrencyTrade(), order.getPrice()));

        uri = "https://www.cryptopia.co.nz/api/GetMarketOrders/KRB_BTC";
        MarketOrders cryptopiaResult = template.getForObject(uri, MarketOrders.class);

        log.debug("{} from cryptopia with {} results",
                cryptopiaResult.getSuccess() ? "success" : "fail",
                cryptopiaResult.getData().getBuy().size());

        uri = "https://api.livecoin.net/exchange/order_book?currencyPair=KRB/BTC";
//         = null;
        try {
            OrderBook livecoinResult = template.getForObject(uri, OrderBook.class);
            log.debug("{) results from livecoin", livecoinResult.getAsks().size());
            livecoinResult.getAsks().forEach(ask -> log.debug("Asc {} ", ask));
        } catch (RestClientException e) {
            log.warn("Error from provider: {}", e.getMessage());
        }

    }
}
