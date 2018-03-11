package org.rublin.provider.com.tradeogre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.controller.api.TradeogreApi;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.OrderResponseDto;
import org.rublin.dto.PairDto;
import org.rublin.dto.RateDto;
import org.rublin.provider.AbstractMarketplace;
import org.rublin.service.CallExecutor;
import org.rublin.utils.RateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Component("tradeogre")
@RequiredArgsConstructor
public class TradeogreMarketplace extends AbstractMarketplace {

    @Value("${provider.tradeogre.pair}")
    private String pair;

    private final CallExecutor callExecutor;
    private final TradeogreApi tradeApi;

    @Override
    public TradePlatform name() {
        return TradePlatform.TRADEOGRE;
    }

    @Override
    public List<OrderResponseDto> trades() {
        return pairs.stream()
                .map(this::createOrderResponse)
                .flatMap(List::stream)
                .collect(toList());
    }

    @Override
    public List<OptimalOrderDto> tradesByPair(PairDto pair) {
        throw new RuntimeException("Deprecated method");
    }

    @Override
    public List<String> getAvailablePairs() {
        return pairs;
    }

    @Override
    public List<RateDto> rates() {
        Map<Currency, RateDto> rates = pairs.stream()
                .map(this::rateByPair)
                .filter(Objects::nonNull)
                .collect(toMap(RateDto::getOrigin, Function.identity()));
        return RateUtil.calcAdditionalRates(rates);
    }

    List<OrderResponseDto> createOrderResponse(String pairString) {
        TradeorgeOrders orders = callExecutor.execute(
                tradeApi.orders(pairString)
        );
        String[] pair = pairString.split("-");
        PairDto buyPair = PairDto.builder()
                .sellCurrency(Currency.valueOf(pair[0]))
                .buyCurrency(Currency.valueOf(pair[1]))
                .build();
        PairDto sellPair = PairDto.builder()
                .sellCurrency(Currency.valueOf(pair[1]))
                .buyCurrency(Currency.valueOf(pair[0]))
                .build();
        List<OptimalOrderDto> buy = orderResponse(orders.getBuy(), buyPair);
        List<OptimalOrderDto> sell = orderResponse(orders.getSell(), sellPair);
        RateDto rate = RateUtil.createRate(buy.get(0), sell.get(0), Currency.valueOf(pair[0]), name());
        return Arrays.asList(OrderResponseDto.builder()
                        .marketplace(name())
                        .pair(buyPair)
                        .rate(rate)
                        .orderList(buy)
                        .build(),
                OrderResponseDto.builder()
                        .marketplace(name())
                        .pair(sellPair)
                        .rate(rate)
                        .orderList(sell)
                        .build()
        );
    }

    private RateDto rateByPair(String pair) {
        return createOrderResponse(pair).get(0).getRate();
    }

    private List<OptimalOrderDto> orderResponse(Map<BigDecimal, BigDecimal> map, PairDto pair) {
        List<OptimalOrderDto> optimalOrders = map.entrySet().stream()
                .map(entry -> OptimalOrderDto.builder()
                        .amountToBuy(pair.isBought() ? entry.getValue() : entry.getKey().multiply(entry.getValue()))
                        .amountToSale(pair.isBought() ? entry.getKey().multiply(entry.getValue()) : entry.getValue())
                        .rate(entry.getKey())
                        .marketplace(TradePlatform.TRADEOGRE.name())
                        .build())
                .sorted(((o1, o2) ->
                        pair.isBought() ?
                                (o2.getRate().compareTo(o1.getRate())) :
                                (o1.getRate().compareTo(o2.getRate()))))
                .collect(toList());
        log.info("Found {} {} orders", optimalOrders.size(), pair.isBought() ? "bought" : "sell");
        return optimalOrders;
    }

    @PostConstruct
    private void init() {
        String[] pairArray = pair.split(SPLIT);
        pairs = Arrays.asList(pairArray);
    }
}
