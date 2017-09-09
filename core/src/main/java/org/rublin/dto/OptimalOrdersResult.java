package org.rublin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OptimalOrdersResult {
    private PairDto pair;
    private BigDecimal amount;
    private List<OptimalOrderDto> optimalOrders;
}
