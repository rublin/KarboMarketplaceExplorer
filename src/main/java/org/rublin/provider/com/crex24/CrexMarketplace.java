package org.rublin.provider.com.crex24;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.controller.api.CrexApi;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Component("crex")
@RequiredArgsConstructor
public class CrexMarketplace extends AbstractMarketplace {

    private static final String INTERNAL_SPLIT = "_";

    @Value("${provider.crex.pair}")
    private String pair;

    private final CallExecutor executor;
    private final CrexApi api;

    @Override
    public TradePlatform name() {
        return TradePlatform.CREX_24;
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
        throw new RuntimeException("Do not use deprecated method");
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
        OrderBook orders = executor.execute(
                api.orders()
        );
        String[] pair = pairString.split(INTERNAL_SPLIT);
        PairDto buyPair = PairDto.builder()
                .sellCurrency(Currency.valueOf(pair[0]))
                .buyCurrency(Currency.valueOf(pair[1]))
                .build();
        PairDto sellPair = PairDto.builder()
                .sellCurrency(Currency.valueOf(pair[1]))
                .buyCurrency(Currency.valueOf(pair[0]))
                .build();
        List<OptimalOrderDto> sell = orderResponse(orders.getBuyOrders(), sellPair);
        List<OptimalOrderDto> buy = orderResponse(orders.getSellOrders(), buyPair);
        RateDto rate = RateUtil.createRate(buy.get(0), sell.get(0), Currency.valueOf(pair[0]), name());
        log.info("{} returns {} buy and {} sell orders",
                name(),
                buy.size(),
                sell.size());

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

    private List<OptimalOrderDto> orderResponse(List<Order> orders, PairDto pair) {
        return orders.stream()
                .map(order -> OptimalOrderDto.builder()
                        .amountToBuy(pair.isBought() ? order.getAmount() : order.getPrice().multiply(order.getAmount()))
                        .amountToSale(pair.isBought() ? order.getPrice().multiply(order.getAmount()) : order.getAmount())
                        .rate(order.getPrice())
                        .marketplace(name().name())
                        .build())
                .sorted(((o1, o2) ->
                        pair.isBought() ?
                                (o1.getRate().compareTo(o2.getRate())) :
                                (o2.getRate().compareTo(o1.getRate()))))
                .collect(toList());
    }

    private RateDto rateByPair(String pair) {
        return createOrderResponse(pair).get(0).getRate();
    }

    @PostConstruct
    private void init() {
        String[] pairArray = pair.split(SPLIT);
        pairs = Arrays.asList(pairArray);
    }
}
