package org.rublin.service;

import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.OptimalOrdersResult;
import org.rublin.dto.PairDto;
import org.rublin.provider.Marketplace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private Marketplace marketplace;

    @Override
    public OptimalOrdersResult findOptimalOrders(PairDto pair, BigDecimal amount) {
        Map<TradePlatform, List<OptimalOrderDto>> tradeMap = marketplace.tradesByPair(pair);
        List<OptimalOrderDto> optimalOrderDtos = tradeMap.get(TradePlatform.BTC_TRADE);
        Comparator<OptimalOrderDto> byRate = new Comparator<OptimalOrderDto>() {
            @Override
            public int compare(OptimalOrderDto o1, OptimalOrderDto o2) {
                return o2.getRate().compareTo(o1.getRate());
            }
        };
        Collections.sort(optimalOrderDtos, byRate);
        List<OptimalOrderDto> limitedOrders = new LinkedList<>();
        BigDecimal ordered = BigDecimal.ZERO;
        int i = 0;
        while (ordered.compareTo(amount) < 0) {
            OptimalOrderDto order = optimalOrderDtos.get(i);
            ordered = ordered.add(order.getAmountToSale());
            limitedOrders.add(order);
            i++;
        }
        return OptimalOrdersResult.builder()
                .pair(pair)
                .amount(amount)
                .optimalOrders(limitedOrders)
                .build();
    }
}
