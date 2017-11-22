package org.rublin.service;

import org.rublin.dto.RateResponseDto;

public interface RateService {

    /**
     * Get {@link RateResponseDto} current rate from cache
     *
     * @return {@link RateResponseDto}
     */
    RateResponseDto getCurrentRate();

//    void updateCacheRate();
}
