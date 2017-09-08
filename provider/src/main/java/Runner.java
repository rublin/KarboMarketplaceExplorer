import lombok.extern.slf4j.Slf4j;
import nz.co.cryptopia.MarketOrders;
import org.springframework.web.client.RestTemplate;
import ua.com.btctrade.TradesBuyPair;

@Slf4j
public class Runner {

    public static void main(String[] args) {

        String uri = "https://btc-trade.com.ua/api/trades/sell/krb_uah";
        RestTemplate template = new RestTemplate();
        TradesBuyPair result = template.getForObject(uri, TradesBuyPair.class);

        log.debug("Success with {} results. Min price {}, Max price {}", result.getTrades().size(), result.getMinPrice(), result.getMaxPrice());
        result.getTrades().forEach(order -> log.debug("Order amount {} with price {}", order.getCurrencyTrade(), order.getPrice()));

        uri = "https://www.cryptopia.co.nz/api/GetMarketOrders/KRB_BTC";
        MarketOrders cryptopiaResult = template.getForObject(uri, MarketOrders.class);

        log.debug("{} from cryptopia with {} results", cryptopiaResult.getSuccess() ? "success" : "fail", cryptopiaResult.getData().getMBuy().size());
    }
}
