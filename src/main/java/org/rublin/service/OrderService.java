package org.rublin.service;

import org.rublin.dto.OptimalOrdersResult;
import org.rublin.dto.PairDto;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    /**
     * Optimal orders {@link OptimalOrdersResult} for specific {@link PairDto} and amount
     * @param pair specific {@link PairDto}
     * @param amount {@link BigDecimal} amount
     * @return {@link OptimalOrdersResult}
     */
    List<OptimalOrdersResult> findOptimalOrders(PairDto pair, BigDecimal amount);
}
