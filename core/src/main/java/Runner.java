import org.springframework.web.client.RestTemplate;
import ua.com.btctrade.TradesBuyPair;

public class Runner {

    public static void main(String[] args) {

        String uri = "https://btc-trade.com.ua/api/trades/sell/btc_uah";
        RestTemplate restTemplate = new RestTemplate();
        TradesBuyPair result = restTemplate.getForObject(uri, TradesBuyPair.class);

        System.out.println(result.getList().size());

    }
}
