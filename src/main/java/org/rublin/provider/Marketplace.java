package org.rublin.provider;

import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.OrderResponseDto;
import org.rublin.dto.PairDto;
import org.rublin.dto.RateDto;

import java.util.List;
import java.util.Map;

public interface Marketplace {

    /**
     * Name of the {@link TradePlatform}
     *
     * @return {@link TradePlatform}
     */
    TradePlatform name();

    /**
     * Orders for all pairs
     *
     * @return {@link List} of {@link OrderResponseDto}
     */
    List<OrderResponseDto> trades();

    /**
     * Orders by {@link PairDto}. {@link Deprecated} use {@link #trades()}
     *
     * @param pair
     * @return
     */
    @Deprecated
    List<OptimalOrderDto> tradesByPair(PairDto pair);

    /**
     * Available pairs for this marketplace
     *
     * @return
     */
    List<String> getAvailablePairs();

    /**
     * Rate for {@link org.rublin.Currency#KRB} to other supported by {@link Marketplace}
     *
     * @return
     */
    @Deprecated
    List<RateDto> rates();
}
