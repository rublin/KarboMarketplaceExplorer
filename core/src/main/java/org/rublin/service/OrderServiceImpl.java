package org.rublin.service;

import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.OptimalOrdersResult;
import org.rublin.dto.PairDto;
import org.rublin.provider.Marketplace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    @Qualifier("marketplace")
    private Marketplace marketplace;

    @Override
    public OptimalOrdersResult findOptimalOrders(PairDto pair, BigDecimal amount) {
        List<OptimalOrderDto> orders = marketplace.tradesByPair(pair);
        Comparator<OptimalOrderDto> byRate = new Comparator<OptimalOrderDto>() {
            @Override
            public int compare(OptimalOrderDto o1, OptimalOrderDto o2) {
                return o2.getRate().compareTo(o1.getRate());
            }
        };
        Collections.sort(orders, byRate);
        List<OptimalOrderDto> limitedOrders = new LinkedList<>();
        BigDecimal ordered = BigDecimal.ZERO;
        int i = 0;
        while (ordered.compareTo(amount) < 0) {
            OptimalOrderDto order = orders.get(i);
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
