package org.rublin.dto;

import lombok.Builder;
import org.rublin.TradePlatform;

import java.util.List;

@Builder
public class OrderResponseDto {
    private TradePlatform marketplace;
    private PairDto pair;
    private List<OptimalOrderDto> orderList;
}
