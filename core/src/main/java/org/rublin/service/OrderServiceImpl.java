package org.rublin.service;

import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.OptimalOrdersResult;
import org.rublin.dto.PairDto;
import org.rublin.provider.Marketplace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private Marketplace marketplace;

    @Override
    public OptimalOrdersResult findOptimalOrders(PairDto pair, BigDecimal amount) {
        Map<TradePlatform, List<OptimalOrderDto>> tradeMap = marketplace.tradesByPair(pair);

        return OptimalOrdersResult.builder()
                .pair(pair)
                .amount(amount)
                .optimalOrders(tradeMap.get(TradePlatform.BTC_TRADE))
                .build();
    }
}
