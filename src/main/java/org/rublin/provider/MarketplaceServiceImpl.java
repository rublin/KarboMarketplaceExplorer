package org.rublin.provider;

import lombok.extern.slf4j.Slf4j;
import org.rublin.TradePlatform;
import org.rublin.dto.OrderResponseDto;
import org.rublin.dto.PairDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class MarketplaceServiceImpl implements MarketplaceService {

    private static final long TIMEOUT_SECONDS = 60;
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
    @Qualifier("tradeogre")
    private Marketplace tradeogreMarketplace;

    @Autowired
    @Qualifier("crex")
    protected Marketplace crexMarketplace;

    private Map<TradePlatform, List<OrderResponseDto>> marketplaceCache = new ConcurrentHashMap<>();


    @Override
    public void createCache() {
        List<Marketplace> marketplaces = Arrays.asList(cryptopiaMarketplace,
                livecoinMarketplace,
                btcTradeMarketplace,
                tradeogreMarketplace,
                crexMarketplace);
        ExecutorService executorService = Executors.newFixedThreadPool(marketplaces.size());
        List<CompletableFuture<List<OrderResponseDto>>> futures = marketplaces.stream().map(
                m -> CompletableFuture.supplyAsync(m::trades, executorService)
                .applyToEither(timeoutAfter(TIMEOUT_SECONDS, TimeUnit.SECONDS), Function.identity())
                .exceptionally(error -> {
                    log.warn("Trades failed {}", error);
                    return Collections.emptyList();
                })
        ).collect(toList());

        Map<TradePlatform, List<OrderResponseDto>> collect = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(groupingBy(OrderResponseDto::getMarketplace));
        marketplaceCache.putAll(collect);
    }

    @Override
    public List<OrderResponseDto> orders() {
        List<OrderResponseDto> orders = marketplaceCache.entrySet().stream()
                .map(Map.Entry::getValue)
                .flatMap(List::stream)
                .collect(toList());
        log.info("Found {} orderResponses in cache", orders.size());
        return orders;
    }

    @Override
    public List<OrderResponseDto> orders(TradePlatform platform) {
        List<OrderResponseDto> orders = marketplaceCache.get(platform);
        log.info("Found {} orderResponses by {} platform in cache", orders.size(), platform);
        return orders;
    }

    @Override
    public List<OrderResponseDto> orders(PairDto pair) {
        List<OrderResponseDto> orders = marketplaceCache.entrySet().stream()
                .map(Map.Entry::getValue)
                .flatMap(List::stream)
                .filter(orderResponseDto -> orderResponseDto.getPair().equals(pair))
                .collect(toList());
        log.info("Found {} orderResponses by {} pair in cache", orders.size(), pair);
        return orders;
    }

    private  <T> CompletableFuture<T> timeoutAfter(long timeout, TimeUnit unit) {
        ScheduledThreadPoolExecutor delayer = new ScheduledThreadPoolExecutor(1);

        CompletableFuture<T> result = new CompletableFuture<>();
        delayer.schedule(() -> result.completeExceptionally(new TimeoutException("Timeout after " + timeout)), timeout, unit);
        return result;
    }
}
