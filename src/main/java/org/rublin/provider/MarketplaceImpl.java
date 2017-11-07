package org.rublin.provider;

import lombok.extern.slf4j.Slf4j;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component("marketplace")
public class MarketplaceImpl implements Marketplace {

    @Autowired
    @Qualifier("btc")
    private Marketplace btcTradeMarketplace;

    @Autowired
    @Qualifier("ctyptopia")
    private Marketplace cryptopiaMarketplace;

    @Autowired
    @Qualifier("livecoin")
    private Marketplace livecoinMarketplace;

    @Override
    public List<OptimalOrderDto> tradesByPair(PairDto pair) {
        List<Marketplace> marketplaces = Arrays.asList(cryptopiaMarketplace, livecoinMarketplace, btcTradeMarketplace);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CompletableFuture<List<OptimalOrderDto>> exceptionally1 = CompletableFuture.supplyAsync(() -> btcTradeMarketplace.tradesByPair(pair), executorService)
                .exceptionally(error -> {
                    log.error("Marketplace error with {} pair", pair);
                    return Collections.emptyList();
                });
        CompletableFuture<List<OptimalOrderDto>> exceptionally2 = CompletableFuture.supplyAsync(() -> cryptopiaMarketplace.tradesByPair(pair), executorService)
                .exceptionally(error -> {
                    log.error("Marketplace error with {} pair", pair);
                    return Collections.emptyList();
                });
        CompletableFuture<List<OptimalOrderDto>> exceptionally3 = CompletableFuture.supplyAsync(() -> livecoinMarketplace.tradesByPair(pair), executorService)
                .exceptionally(error -> {
                    log.error("Marketplace error with {} pair", pair);
                    return Collections.emptyList();
                });
        CompletableFuture<List<OptimalOrderDto>> firstCombined = exceptionally1.thenCombine(exceptionally2, this::combine);
        CompletableFuture<List<OptimalOrderDto>> lastCombined = firstCombined.thenCombine(exceptionally3, this::combine);

        List<OptimalOrderDto> result = getResult(lastCombined);
        /*List<OptimalOrderDto> result = marketplaces.parallelStream()
                .map(marketplace -> marketplace.tradesByPair(pair))
                .flatMap(List::stream)
                .collect(toList());*/
        return result;
    }

    private List<OptimalOrderDto> getResult(CompletableFuture<List<OptimalOrderDto>> list) {
        try {
            return list.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    private List<OptimalOrderDto> combine(List<OptimalOrderDto> first, List<OptimalOrderDto> second) {
        List<OptimalOrderDto> resultList = new ArrayList<>(first);
        resultList.addAll(second);
        return resultList;
    }
}
