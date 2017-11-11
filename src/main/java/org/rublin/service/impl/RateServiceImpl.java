package org.rublin.service.impl;

import org.rublin.Currency;
import org.rublin.dto.RateDto;
import org.rublin.dto.RateResponseDto;
import org.rublin.provider.Marketplace;
import org.rublin.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
public class RateServiceImpl implements RateService {

    @Autowired
    @Qualifier("marketplace")
    private Marketplace marketplace;

    @Override
    public RateResponseDto getCurrentRate() {
        List<RateDto> rates = marketplace.rates();
        Map<Currency, List<RateDto>> collect = rates.stream()
                .collect(groupingBy(RateDto::getTarget));

        return RateResponseDto.builder()
                .byCurrency(collect)
                .build();
    }
}
