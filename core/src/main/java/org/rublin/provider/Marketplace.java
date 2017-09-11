package org.rublin.provider;

import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;

import java.util.List;
import java.util.Map;

public interface Marketplace {
    Map<TradePlatform, List<OptimalOrderDto>> tradesByPair(PairDto pair);
}
