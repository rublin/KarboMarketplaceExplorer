package org.rublin.provider.fiat.privat;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.dto.PairDto;
import org.rublin.dto.RateDto;
import org.rublin.provider.FiatRate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class PrivatRate implements FiatRate {

    private static final String URL = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=4";
    private static final String NAME = "Privat bank";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public List<RateDto> allRates() {
        List<RateDto> allSupportedRates = getRate().stream()
                .map(this::convert)
                .filter(Objects::nonNull)
                .collect(toList());
        log.info("{} returned {} supported rates", name(), allSupportedRates.size());
        return allSupportedRates;
    }

    @Override
    public RateDto rate(PairDto pair) {
        return null;
    }

    private List<PrivatRateRsp> getRate() {

        RestTemplate template = new RestTemplate();
        try {
            PrivatRateRsp[] result = template.getForObject(URL, PrivatRateRsp[].class);
            log.info("{} receive {} results", name(), result.length);
            return Arrays.asList(result);
        } catch (RestClientException e) {
            log.warn("{} request {} error {}", name(), URL, e.getMessage());
        }

        return Collections.emptyList();
    }

    private RateDto convert(PrivatRateRsp rsp) {
        Currency origin = Currency.getCurrency(rsp.getFrom());
        Currency target = Currency.getCurrency(rsp.getTo());
        if (Objects.nonNull(origin) && Objects.nonNull(target)) {
            return RateDto.builder()
                    .origin(origin)
                    .target(target)
                    .buyRate(rsp.getSale())
                    .saleRate(rsp.getBuy())
                    .build();

        }

        return null;
    }
}
