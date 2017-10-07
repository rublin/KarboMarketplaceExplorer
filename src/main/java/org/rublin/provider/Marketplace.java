package org.rublin.provider;

import org.rublin.Currency;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;
import org.rublin.dto.RateDto;

import java.util.List;

public interface Marketplace {
    List<OptimalOrderDto> tradesByPair(PairDto pair);
}
