package org.rublin.service;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.OptimalOrdersResult;
import org.rublin.dto.PairDto;
import org.rublin.dto.RateDto;
import org.rublin.provider.Marketplace;
import org.rublin.provider.cryptonator.CryptonatorRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    @Qualifier("marketplace")
    private Marketplace marketplace;

    private static final long TIMEOUT_SECONDS = 5;
    @Autowired
    @Qualifier("btc")
    private Marketplace btcTradeMarketplace;

    @Autowired
    @Qualifier("ctyptopia")
    private Marketplace cryptopiaMarketplace;

    @Autowired
    @Qualifier("livecoin")
    private Marketplace livecoinMarketplace;

    @Autowired
    private CryptonatorRate cryptonator;

    private List<Marketplace> marketplaces = Arrays.asList(cryptopiaMarketplace, livecoinMarketplace, btcTradeMarketplace);

    @Override
    public OptimalOrdersResult findOptimalOrders(PairDto pair, BigDecimal amount) {
        List<Marketplace> singleReq = new ArrayList<>();
        List<Marketplace> doubleRequest = new ArrayList<>();
        for (Marketplace m : marketplaces) {
            boolean sale = false;
            boolean buy = false;
            for (String s : m.getAvailablePairs()) {
                if (s.contains(pair.getSellCurrency().name()) && s.contains(pair.getBuyCurrency().name())) {
                    singleReq.add(m);
                    break;
                }
                if (s.contains(pair.getSellCurrency().name()))
                    sale = true;
                else if (s.contains(pair.getBuyCurrency().name()))
                    buy = true;
                if (sale && buy)
                    break;
            }
        }

        List<OptimalOrderDto> orders = tradesByPair(singleReq, pair);
        if (singleReq.size() != marketplaces.size()) {

        }
        sortOrders(orders, pair);
        List<OptimalOrderDto> limitedOrders = new LinkedList<>();
        BigDecimal sellAmount = BigDecimal.ZERO;
        BigDecimal boughtAmount = BigDecimal.ZERO;
        for (OptimalOrderDto order : orders) {
            limitedOrders.add(order);
            if (sellAmount.add(order.getAmountToSale()).compareTo(amount) < 0) {
                sellAmount = sellAmount.add(order.getAmountToSale());
                boughtAmount = boughtAmount.add(order.getAmountToBuy());
            } else {
                BigDecimal remainder = amount.subtract(sellAmount);
                sellAmount = sellAmount.add(remainder);
                if (pair.isBought()) {
                    boughtAmount = boughtAmount.add(remainder.divide(order.getRate(), BigDecimal.ROUND_HALF_UP));
                } else {
                    boughtAmount = boughtAmount.add(remainder.multiply(order.getRate()));
                }
                break;
            }
        }

        return OptimalOrdersResult.builder()
                .pair(pair)
                .amountSell(sellAmount.stripTrailingZeros())
                .amountBuy(boughtAmount.stripTrailingZeros())
                .averageRate(rate(sellAmount, boughtAmount, pair))
                .optimalOrders(limitedOrders)
                .build();
    }

    @Override
    public RateDto getRate(Currency target) {
        return cryptonator.krbRate(target);
    }

    @Override
    public List<RateDto> rates() {
        List<RateDto> resultRates = Arrays.stream(Currency.values())
                .filter(currency -> currency != Currency.KRB)
                .map(this::getRate)
                .filter(Objects::nonNull)
                .collect(toList());
        return resultRates;
    }

    protected void sortOrders(List<OptimalOrderDto> orders, PairDto pair) {
        Comparator<OptimalOrderDto> toSell = new Comparator<OptimalOrderDto>() {
            @Override
            public int compare(OptimalOrderDto o1, OptimalOrderDto o2) {
                return o2.getRate().compareTo(o1.getRate());
            }
        };
        Comparator<OptimalOrderDto> toBuy = new Comparator<OptimalOrderDto>() {
            @Override
            public int compare(OptimalOrderDto o1, OptimalOrderDto o2) {
                return o1.getRate().compareTo(o2.getRate());
            }
        };
        if (pair.isBought()) {
            Collections.sort(orders, toBuy);
        } else {
            Collections.sort(orders, toSell);
        }
    }

    protected BigDecimal rate(BigDecimal sell, BigDecimal buy, PairDto pairDto) {
        if (pairDto.isBought()) {
            BigDecimal divide = sell.divide(buy, BigDecimal.ROUND_HALF_UP);
            return divide.stripTrailingZeros();
        }
        return buy.divide(sell, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
    }

    public List<OptimalOrderDto> tradesByPair(List<Marketplace> marketplaces, PairDto pair) {
        ExecutorService executorService = Executors.newFixedThreadPool(marketplaces.size());

        List<CompletableFuture<List<OptimalOrderDto>>> futures = marketplaces.stream().map(
                m -> CompletableFuture.supplyAsync(() ->
                        m.tradesByPair(pair), executorService)
                        .applyToEither(timeoutAfter(TIMEOUT_SECONDS, TimeUnit.SECONDS), Function.identity())
                        .exceptionally(error -> {
                            log.warn("Failed getOneSegmentDetails: " + error);
                            return Collections.emptyList();
                        }))
                .collect(Collectors.toList());

        List<OptimalOrderDto> collect = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        executorService.shutdown();
        return collect;
    }

    private <T> CompletableFuture<T> timeoutAfter(long timeout, TimeUnit unit) {
        ScheduledThreadPoolExecutor delayer = new ScheduledThreadPoolExecutor(1);

        CompletableFuture<T> result = new CompletableFuture<>();
        delayer.schedule(() -> result.completeExceptionally(new TimeoutException("Timeout after " + timeout)), timeout, unit);
        return result;
    }

    private List<Marketplace> marketplacesByPair(PairDto pair, int depth) {
        return marketplaces.stream()
                .filter(m -> isHavePair(m, pair))
                .collect(toList());
    }

    private boolean isHavePair(Marketplace marketplace, PairDto pair) {
        return marketplace.getAvailablePairs().stream()
                .anyMatch(s ->
                        s.contains(pair.getBuyCurrency()
                                .name()) &&
                                s.contains(pair.getSellCurrency()
                                        .name()
                                ));
    }

}
