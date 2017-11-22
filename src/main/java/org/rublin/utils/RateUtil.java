package org.rublin.utils;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.RateDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class RateUtil {
    public static List<RateDto> calcAdditionalRates(Map<Currency, RateDto> rates) {
        List<RateDto> withAdditional = new ArrayList<>();
        RateDto krbRate = rates.get(Currency.KRB);
        if (Objects.isNull(krbRate)) {
            return withAdditional;
        }
        for (Currency currency : Currency.values()) {
            RateDto rate = rates.get(currency);
            if (Objects.nonNull(rate)) {
                if (currency == Currency.KRB) {
                    withAdditional.add(rate);
                } else if (rate.getTarget() == krbRate.getTarget()) {
                    RateDto additionalRate = RateDto.builder()
                            .origin(Currency.KRB)
                            .target(rate.getOrigin())
                            .saleRate(krbRate.getSaleRate().divide(rate.getSaleRate(), BigDecimal.ROUND_HALF_UP))
                            .buyRate(krbRate.getBuyRate().divide(rate.getBuyRate(), BigDecimal.ROUND_HALF_UP))
                            .marketplace(TradePlatform.BTC_TRADE)
                            .info(String.format("(%s > %s > %s)",
                                    rate.getOrigin(),
                                    rate.getTarget(),
                                    krbRate.getOrigin()))
                            .build();
                    log.info("Created additional {} rate {} - {}", currency, additionalRate.getSaleRate(), additionalRate.getBuyRate());
                    withAdditional.add(additionalRate);
                }
            }
        }
        return withAdditional;
    }

    public static RateDto createRate(OptimalOrderDto buy, OptimalOrderDto sell, Currency target, TradePlatform platform) {
        return RateDto.builder()
                .origin(Currency.KRB)
                .target(target)
                .saleRate(sell.getRate())
                .buyRate(buy.getRate())
                .marketplace(platform)
                .build();
    }
}
