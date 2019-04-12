package org.rublin.provider.com.richamster;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.controller.api.RichamsterApi;
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
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Component("richamster")
@RequiredArgsConstructor
public class RichamsterMarketplace extends AbstractMarketplace {

    private static final String INTERNAL_SPLIT = "/";

    private final CallExecutor executor;
    private final RichamsterApi api;

    @Value("${provider.richamster.pair}")
    private String pair;

    @Override
    public TradePlatform name() {
        return TradePlatform.RICHAMSTER;
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

    private RateDto rateByPair(String pair) {
        return createOrderResponse(pair).get(0).getRate();
    }

    private List<OrderResponseDto> createOrderResponse(String pairString) {
        RichamsterOrderBook orders = executor.execute(api.orders(pairString.toLowerCase()));
        String[] pair = pairString.split(INTERNAL_SPLIT);
        PairDto sellPair = PairDto.builder()
                .sellCurrency(Currency.valueOf(pair[0]))
                .buyCurrency(Currency.valueOf(pair[1]))
                .build();
        PairDto buyPair = PairDto.builder()
                .sellCurrency(Currency.valueOf(pair[1]))
                .buyCurrency(Currency.valueOf(pair[0]))
                .build();
        List<OptimalOrderDto> sell = orderResponse(orders.getBuying());
        List<OptimalOrderDto> buy = orderResponse(orders.getSelling());
        RateDto rate = RateUtil.createRate(buy.get(0), sell.get(0), Currency.valueOf(pair[1]), name());
        log.info("{} returns {} buy and {} sell orders", name(), buy.size(), sell.size());
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
                        .build());
    }

    private List<OptimalOrderDto> orderResponse(List<Order> orders) {
        boolean buying = orders.get(0).getSide().equals("buying");
        return orders.stream()
                .map(order -> OptimalOrderDto.builder()
                        .amountToBuy(buying ? order.getSum() : order.getVolume())
                        .amountToSale(buying ? order.getVolume() : order.getSum())
                        .rate(order.getPrice())
                        .marketplace(name().name())
                        .build())
                .sorted((o1, o2) ->
                        buying ?
                                (o2.getRate().compareTo(o1.getRate())) :
                                (o1.getRate().compareTo(o2.getRate()))
                ).collect(Collectors.toList());
    }

    @PostConstruct
    private void init() {
        String[] pairArray = pair.split(SPLIT);
        pairs = Arrays.asList(pairArray);
    }
}
