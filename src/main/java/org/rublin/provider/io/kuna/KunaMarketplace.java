package org.rublin.provider.io.kuna;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rublin.TradePlatform;
import org.rublin.controller.api.CrexApi;
import org.rublin.controller.api.KunaApi;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.OrderResponseDto;
import org.rublin.dto.PairDto;
import org.rublin.dto.RateDto;
import org.rublin.provider.AbstractMarketplace;
import org.rublin.service.CallExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component("kuna")
@RequiredArgsConstructor
public class KunaMarketplace extends AbstractMarketplace {

    public static final String KUNA_URL = "https://kuna.io/api/v2/depth";

    private final CallExecutor executor;
    private final KunaApi api;

    @Value("${provider.kuna.pair}")
    private String pair;

    @Override
    public TradePlatform name() {
        return TradePlatform.KUNA;
    }

    @Override
    public List<OrderResponseDto> trades() {
        return pairs.stream()
                .map(this::createOrderResponse)
                .flatMap(List::stream)
                .collect(toList());
    }

    private List<OrderResponseDto> createOrderResponse(String pairString) {
        KunaOrderBook orders = executor.execute(api.orders(pairString));

    }

    @Override
    public List<OptimalOrderDto> tradesByPair(PairDto pair) {
        return null;
    }

    @Override
    public List<RateDto> rates() {
        return null;
    }

    @PostConstruct
    private void init() {
        String[] pairArray = pair.split(SPLIT);
        pairs = Arrays.asList(pairArray);
    }
}
