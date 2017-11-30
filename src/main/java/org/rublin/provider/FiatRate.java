package org.rublin.provider;

import org.rublin.dto.PairDto;
import org.rublin.dto.RateDto;

import java.util.List;

public interface FiatRate {

    /**
     * Get name
     * @return {@link String}
     */
    String name();

    /**
     * All rates
     * @return {@link List} of {@link RateDto}
     */
    List<RateDto> allRates();

    /**
     * {@link RateDto} by {@link PairDto}
     * @param pair
     * @return
     */
    RateDto rate(PairDto pair);
}
