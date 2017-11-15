package org.rublin.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.dto.RateDto;
import org.rublin.dto.RateResponseDto;
import org.rublin.provider.Marketplace;
import org.rublin.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
public class RateServiceImpl implements RateService {

    @Autowired
    @Qualifier("marketplace")
    private Marketplace marketplace;

    @Override
    @Cacheable("rate")
    public RateResponseDto getCurrentRate() {
        long start = System.currentTimeMillis();
        List<RateDto> rates = marketplace.rates();
        Map<Currency, List<RateDto>> collect = rates.stream()
                .collect(groupingBy(RateDto::getTarget));
        log.info("{} rate results received by {} ms", rates.size(), System.currentTimeMillis() - start);
        return RateResponseDto.builder()
                .byCurrency(collect)
                .build();
    }
}
