package org.rublin.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.TradePlatform;
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
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
public class RateServiceImpl implements RateService {

    public RateResponseDto rateResponse;

    @Autowired
    @Qualifier("marketplace")
    private Marketplace marketplace;

    @Override
    public RateResponseDto getCurrentRate() {
        long start = System.currentTimeMillis();
        if (Objects.isNull(rateResponse)) {
            updateCacheRate();
        }
        log.info("rate results for {} marketplaces and {} currencies received by {} ms",
                rateResponse.getByMarketplace().size(),
                rateResponse.getByCurrency().size(),
                System.currentTimeMillis() - start);

        return rateResponse;
    }

    @Override
    public void updateCacheRate() {
        long start = System.currentTimeMillis();
        List<RateDto> rates = marketplace.rates();
        Map<Currency, List<RateDto>> byCurrency = rates.stream()
                .collect(groupingBy(RateDto::getTarget));
        Map<TradePlatform, List<RateDto>> byMarketplace = rates.stream()
                .collect(groupingBy(RateDto::getMarketplace));
        rateResponse = RateResponseDto.builder()
                .byCurrency(byCurrency)
                .byMarketplace(byMarketplace)
                .build();
        log.info("rate results for {} marketplaces and {} currencies received by {} ms",
                rateResponse.getByMarketplace().size(),
                rateResponse.getByCurrency().size(),
                System.currentTimeMillis() - start);
    }
}
