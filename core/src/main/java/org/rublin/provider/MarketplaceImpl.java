package org.rublin.provider;

import lombok.extern.slf4j.Slf4j;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("marketplace")
public class MarketplaceImpl implements Marketplace {
    public static final String LIVECOIN = "https://api.livecoin.net/exchange/order_book?currencyPair=KRB/BTC";

    @Autowired
    @Qualifier("btc")
    private Marketplace btcTradeMarketplace;

    @Autowired
    @Qualifier("ctyptopia")
    private Marketplace cryptopiaMarketplace;

    @Override
    public List<OptimalOrderDto> tradesByPair(PairDto pair) {
        List<OptimalOrderDto> result = new ArrayList<>();
        result.addAll(btcTradeMarketplace.tradesByPair(pair));
        result.addAll(cryptopiaMarketplace.tradesByPair(pair));

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
