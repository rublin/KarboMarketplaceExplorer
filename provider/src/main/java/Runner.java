import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import ua.com.btctrade.TradesBuyPair;

@Slf4j
public class Runner {

    public static void main(String[] args) {

        String uri = "https://btc-trade.com.ua/api/trades/sell/btc_uah";
        RestTemplate restTemplate = new RestTemplate();
        TradesBuyPair result = restTemplate.getForObject(uri, TradesBuyPair.class);

        log.debug("Success with {} results. Min price {}, Max price {}", result.getList().size(), result.getMin_price(), result.getMax_price());
        result.getList().forEach(order -> log.debug("Order amount {} with price {}", order.getCurrency_trade(), order.getPrice()));
    }
}
