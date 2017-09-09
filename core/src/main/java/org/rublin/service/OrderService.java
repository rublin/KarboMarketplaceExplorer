package org.rublin.service;

import org.rublin.dto.OptimalOrdersResult;
import org.rublin.dto.PairDto;

import java.math.BigDecimal;

public interface OrderService {
    OptimalOrdersResult findOptimalOrders(PairDto pair, BigDecimal amount);
}
