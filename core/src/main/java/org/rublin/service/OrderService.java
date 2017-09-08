package org.rublin.service;

import org.rublin.dto.PairDto;

import java.math.BigDecimal;

public interface OrderService {
    void findOptimalOrders(PairDto pair, BigDecimal amount);
}
