package org.rublin.service;

import org.rublin.Currency;
import org.rublin.dto.OptimalOrdersResult;
import org.rublin.dto.PairDto;
import org.rublin.dto.RateDto;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    OptimalOrdersResult findOptimalOrders(PairDto pair, BigDecimal amount);

    RateDto getRate(Currency target);

    List<RateDto> rates();
}
