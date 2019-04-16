package org.rublin.provider;

import lombok.extern.slf4j.Slf4j;
import org.rublin.TradePlatform;
import org.rublin.dto.OrderResponseDto;
import org.rublin.dto.PairDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class MarketplaceServiceImpl implements MarketplaceService {

    private static final long TIMEOUT_SECONDS = 60;

    @Value("${provider.btc-trade.enable}")
    private boolean btcTradeEnabled;

    @Value("${provider.crex.enable}")
    private boolean crexEnabled;

    @Value("${provider.cryptopia.enable}")
    private boolean cryptopiaEnabled;

    @Value("${provider.kuna.enable}")
    private boolean kunaEnabled;

    @Value("${provider.livecoin.enable}")
    private boolean livecoinEnabled;

    @Value("${provider.richamster.enable}")
    private boolean richamsterEnabled;

    @Value("${provider.tradeogre.enable}")
    private boolean tradeogreEnabled;

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

    @Autowired
    @Qualifier("kuna")
    protected Marketplace kunaMarketplace;

    @Autowired
    @Qualifier("richamster")
    protected Marketplace richamsterMarketplace;

    private Map<TradePlatform, List<OrderResponseDto>> marketplaceCache = new ConcurrentHashMap<>();
    private List<Marketplace> marketplaces;
    private ExecutorService executorService;
    private ScheduledThreadPoolExecutor delayedThreadPool;

    @Override
    public void createCache() {
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

    private <T> CompletableFuture<T> timeoutAfter(long timeout, TimeUnit unit) {
        CompletableFuture<T> result = new CompletableFuture<>();
        delayedThreadPool.schedule(() -> result.completeExceptionally(new TimeoutException("Timeout after " + timeout)), timeout, unit);
        return result;
    }

    @PostConstruct
    private void init() {
        marketplaces = new ArrayList<>();
        if (btcTradeEnabled)
            marketplaces.add(btcTradeMarketplace);
        if (crexEnabled)
            marketplaces.add(crexMarketplace);
        if (cryptopiaEnabled)
            marketplaces.add(cryptopiaMarketplace);
        if (kunaEnabled)
            marketplaces.add(kunaMarketplace);
        if (livecoinEnabled)
            marketplaces.add(livecoinMarketplace);
        if (richamsterEnabled)
            marketplaces.add(richamsterMarketplace);
        if (tradeogreEnabled)
            marketplaces.add(tradeogreMarketplace);

        delayedThreadPool = new ScheduledThreadPoolExecutor(marketplaces.size());
        executorService = Executors.newFixedThreadPool(marketplaces.size(), r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });
    }
}