package org.rublin.provider.btctrade;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;
import org.rublin.dto.RateDto;
import org.rublin.provider.Marketplace;
import org.rublin.utils.RateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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
    public List<String> getAvailablePairs() {
        return btctradePair;
    }

    @Override
    public List<RateDto> rates() {
        Map<Currency, RateDto> rates = btctradePair.stream()
//                .filter(s -> s.contains("KRB"))
                .map(this::rateByPair)
                .filter(Objects::nonNull)
                .collect(toMap(RateDto::getOrigin, Function.identity()));
        return RateUtil.calcAdditionalRates(rates);
    }

    private RateDto rateByPair(String pair) {
        String[] pairs = pair.split("_");
        Optional<TradesBuyPair> sellResponse = getResponse(BTC_TRADE_URL.concat("sell/").concat(pair));
        Optional<TradesBuyPair> buyResponse = getResponse(BTC_TRADE_URL.concat("buy/").concat(pair));
        if (sellResponse.isPresent() && buyResponse.isPresent()) {
            BigDecimal buy = sellResponse.get().getMinPrice();
            BigDecimal sell = buyResponse.get().getMaxPrice();
            RateDto rate = RateDto.builder()
                    .saleRate(sell)
                    .buyRate(buy)
                    .origin(Currency.valueOf(pairs[0]))
                    .target(Currency.valueOf(pairs[1]))
                    .marketplace(TradePlatform.BTC_TRADE)
                    .build();
            log.info("Receive {}-{} rate {} {}", rate.getOrigin(), rate.getTarget(), rate.getSaleRate(), rate.getBuyRate());
            return rate;
        }

        return null;
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
        String url = BTC_TRADE_URL;
        if (pair.isBought()) {
            url = url.concat("sell/").concat(supportedPair.get());
        } else {
            url = url.concat("buy/").concat(supportedPair.get());
        }
        TradesBuyPair btcTradeResult = getResponse(url).orElse(null);

        if (Objects.isNull(btcTradeResult)) {
            return result;
        }

        if (btcTradeResult != null) {
            log.info("{} returns {} orders", TradePlatform.BTC_TRADE, btcTradeResult.getTrades().size());
            List<OptimalOrderDto> orders = btcTradeResult.getTrades().stream()
                    .map(trade -> OptimalOrderDto.builder()
                            .marketplace(TradePlatform.BTC_TRADE.name())
                            .amountToSale(pair.isBought() ? trade.getCurrencyBase() : trade.getCurrencyTrade())
                            .amountToBuy(pair.isBought() ? trade.getCurrencyTrade() : trade.getCurrencyBase())
                            .rate(trade.getPrice())
                            .build())
                    .collect(toList());
            result.addAll(orders);
        }
        return result;
    }

    private Optional<TradesBuyPair> getResponse(String url) {
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
}
