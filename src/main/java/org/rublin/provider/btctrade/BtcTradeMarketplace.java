package org.rublin.provider.btctrade;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.OrderResponseDto;
import org.rublin.dto.PairDto;
import org.rublin.dto.RateDto;
import org.rublin.model.BtcTradePairsEntity;
import org.rublin.provider.AbstractMarketplace;
import org.rublin.utils.RateUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Component("btc")
public class BtcTradeMarketplace extends AbstractMarketplace {

    public static final String BTC_TRADE_URL = "https://btc-trade.com.ua/api/trades/";
    private static final String INTERNAL_SPLIT = "_";

    private final BtcTradePairsEntity entity;

    public BtcTradeMarketplace(BtcTradePairsEntity entity) {
        this.entity = entity;
        init();
    }

    private void init() {
        String[] pairArray = entity.getPairString().split(",");
        pairs = Arrays.asList(pairArray);
    }

    @Override
    public List<RateDto> rates() {
        Map<Currency, RateDto> rates = pairs.stream()
//                .filter(s -> s.contains("KRB"))
                .map(this::rateByPair)
                .filter(Objects::nonNull)
                .collect(toMap(RateDto::getOrigin, Function.identity()));
        return RateUtil.calcAdditionalRates(rates);
    }

    @Override
    public TradePlatform name() {
        return TradePlatform.BTC_TRADE;
    }

    @Override
    public List<OrderResponseDto> trades() {
        List<OrderResponseDto> trades = pairs.stream()
                .map(this::createOrderResponse)
                .flatMap(List::stream)
                .collect(toList());
        return trades;
    }

    @Override
    public List<OptimalOrderDto> tradesByPair(PairDto pair) {
        List<OptimalOrderDto> result = new ArrayList<>();
        Currency buy = pair.getBuyCurrency();
        Currency sell = pair.getSellCurrency();

        Optional<String> supportedPair = pairs.stream()
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

    @Deprecated
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

    List<OrderResponseDto> createOrderResponse(String pairString) {
        List<OrderResponseDto> orderResult = new ArrayList<>();
        Optional<TradesBuyPair> tradesBuy = tradeOrders(pairString, true);
        Optional<TradesBuyPair> tradesSell = tradeOrders(pairString, false);
        String[] currencies = pairString.split(INTERNAL_SPLIT);
        if (tradesBuy.isPresent() && tradesSell.isPresent()) {
            List<OptimalOrderDto> buy = orders(tradesBuy.get(), true);
            List<OptimalOrderDto> sell = orders(tradesSell.get(), false);
            RateDto rate = RateUtil.createRate(buy.get(0), sell.get(0), Currency.valueOf(currencies[1]), name());
            orderResult.add(OrderResponseDto.builder()
                    .marketplace(name())
                    .pair(PairDto.builder()
                            .buyCurrency(Currency.valueOf(currencies[0]))
                            .sellCurrency(Currency.valueOf(currencies[1]))
                            .build())
                    .orderList(buy)
                    .rate(rate)
                    .build());
            orderResult.add(OrderResponseDto.builder()
                    .marketplace(name())
                    .pair(PairDto.builder()
                            .buyCurrency(Currency.valueOf(currencies[1]))
                            .sellCurrency(Currency.valueOf(currencies[0]))
                            .build())
                    .orderList(sell)
                    .rate(rate)
                    .build());
        }
        return orderResult;
    }

    private List<OptimalOrderDto> orders(TradesBuyPair trades, boolean buy) {
        return  trades.getTrades().stream()
                .map(trade -> OptimalOrderDto.builder()
                        .marketplace(TradePlatform.BTC_TRADE.name())
                        .amountToSale(buy ? trade.getCurrencyBase() : trade.getCurrencyTrade())
                        .amountToBuy(buy ? trade.getCurrencyTrade() : trade.getCurrencyBase())
                        .rate(trade.getPrice())
                        .build())
                .collect(toList());
    }
    private Optional<TradesBuyPair> tradeOrders(String pairString, boolean buy) {
        String url = buy ?
                BTC_TRADE_URL.concat("sell/").concat(pairString.toLowerCase()) :
                BTC_TRADE_URL.concat("buy/").concat(pairString.toLowerCase());
        RestTemplate template = new RestTemplate();
        TradesBuyPair btcTradeResult = null;
        int count = 0;
        while (Objects.isNull(btcTradeResult) || Objects.isNull(btcTradeResult.getTrades()) && count < 3) {
            try {
                log.info("Try to send BTC_TRADE {} req", url);
                btcTradeResult = template.getForObject(url, TradesBuyPair.class);
            } catch (Throwable e) {
                log.warn("BTC_TRADE send error: {}", e.getMessage());
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
