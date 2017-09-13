package org.rublin.provider;

import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;

import java.util.List;

public interface Marketplace {
    List<OptimalOrderDto> tradesByPair(PairDto pair);
}
