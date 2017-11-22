package org.rublin.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OrderResponseDto;
import org.rublin.dto.RateDto;
import org.rublin.dto.RateResponseDto;
import org.rublin.provider.Marketplace;
import org.rublin.provider.MarketplaceService;
import org.rublin.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
public class RateServiceImpl implements RateService {

    @Autowired
    private MarketplaceService marketplaceService;

    @Override
    public RateResponseDto getCurrentRate() {
        long start = System.currentTimeMillis();
        Set<RateDto> uniqueRates = marketplaceService.orders().stream()
                .map(OrderResponseDto::getRate)
                .collect(toSet());
        RateResponseDto rateResponse = createResponse(uniqueRates);

        log.info("rate results for {} marketplaces and {} currencies received by {} ms",
                rateResponse.getByMarketplace().size(),
                rateResponse.getByCurrency().size(),
                System.currentTimeMillis() - start);

        return rateResponse;
    }

    public RateResponseDto createResponse(Set<RateDto> rates) {
        long start = System.currentTimeMillis();

        Map<Currency, List<RateDto>> byCurrency = rates.stream()
                .collect(groupingBy(RateDto::getTarget));
        Map<TradePlatform, List<RateDto>> byMarketplace = rates.stream()
                .collect(groupingBy(RateDto::getMarketplace));
        RateResponseDto rateResponse = RateResponseDto.builder()
                .byCurrency(byCurrency)
                .byMarketplace(byMarketplace)
                .build();
        log.info("rate results for {} marketplaces and {} currencies received by {} ms",
                rateResponse.getByMarketplace().size(),
                rateResponse.getByCurrency().size(),
                System.currentTimeMillis() - start);

        return rateResponse;
    }
}
