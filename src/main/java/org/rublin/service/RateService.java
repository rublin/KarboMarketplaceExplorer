package org.rublin.service;

import org.rublin.dto.RateResponseDto;

public interface RateService {

    RateResponseDto getCurrentRate();

    void updateCacheRate();
}
