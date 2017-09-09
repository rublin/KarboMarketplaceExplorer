package org.rublin.service;

import org.rublin.dto.OptimalOrdersResult;
import org.rublin.dto.PairDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedList;

@Service
public class OrderServiceImpl implements OrderService {

    @Override
    public OptimalOrdersResult findOptimalOrders(PairDto pair, BigDecimal amount) {
        return OptimalOrdersResult.builder().pair(pair).amount(amount).optimalOrders(new LinkedList<>()).build();
    }
}
