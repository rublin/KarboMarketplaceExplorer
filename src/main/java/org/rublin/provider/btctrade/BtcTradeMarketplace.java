package org.rublin.provider.btctrade;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;
import org.rublin.provider.Marketplace;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component("btc")
public class BtcTradeMarketplace implements Marketplace {

    public static final String BTC_TRADE_URL = "https://btc-trade.com.ua/api/trades/";

    @Value("${provider.btc-trade.pair}")
    private String pairString;

    private List<String> btctradePair;

    @PostConstruct
    private void init() {
        String[] pairArray = pairString.split(",");
        btctradePair = Arrays.asList(pairArray);
    }

    @Override
    public List<OptimalOrderDto> tradesByPair(PairDto pair) {
        List<OptimalOrderDto> result = new ArrayList<>();
        Currency buy = pair.getBuyCurrency();
        Currency sell = pair.getSellCurrency();

        Optional<String> supportedPair = btctradePair.stream()
                .filter(s -> s.contains(buy.name()) && s.contains(sell.name()))
                .findFirst();
        if (!supportedPair.isPresent()) {
            return result;
        }

        List<String> urls = createUrl(supportedPair.get(), pair);
        List<TradesBuyPair> pairList = urls.stream()
                .map(this::tradePairs)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());


        if (pairList.isEmpty()) {
            return result;
        }

        result.addAll(convertToOptimalOrders(pairList, pair));
        return result;
    }

    private List<OptimalOrderDto> convertToOptimalOrders(List<TradesBuyPair> pairList, PairDto pair) {
        List<OptimalOrderDto> result = new ArrayList<>();
        /*if (btcTradeResult != null) {
            log.info("{} returns {} orders", TradePlatform.BTC_TRADE, btcTradeResult.getTrades().size());
            List<OptimalOrderDto> orders = btcTradeResult.getTrades().stream()
                    .map(trade -> OptimalOrderDto.builder()
                            .marketplace(TradePlatform.BTC_TRADE.name())
                            .amountToSale(pair.isBought() ? trade.getCurrencyBase() : trade.getCurrencyTrade())
                            .amountToBuy(pair.isBought() ? trade.getCurrencyTrade(): trade.getCurrencyBase())
                            .rate(trade.getPrice())
                            .build())
                    .collect(toList());
            result.addAll(orders);
        }*/
        return result;
    }

    private Optional<TradesBuyPair> tradePairs(String url) {
        RestTemplate template = new RestTemplate();
        TradesBuyPair btcTradeResult = null;
        int count = 0;
        while (Objects.isNull(btcTradeResult) && count < 3
                ) {
            try {
                log.info("Send {} req", url);
                btcTradeResult = template.getForObject(url, TradesBuyPair.class);
            } catch (Throwable e) {
                log.warn("{} error", e.getMessage());
                count++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return Optional.ofNullable(btcTradeResult);
    }

    private List<String> createUrl(String supportedPair, PairDto pair) {
        String[] split = supportedPair.split(">");
        List<String> urls = new ArrayList<>(split.length);
        for (String s : split) {
            String url = BTC_TRADE_URL.concat(s.contains(pair.getSellCurrency().name()) ? "/buy/" : "/sell/").concat(s);
            urls.add(url);
        }
        return urls;
    }
}
