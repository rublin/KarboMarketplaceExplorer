package org.rublin.provider.cryptonator;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.dto.RateDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@Slf4j
public class CryptonatorRate {
    public static final String URL = "https://api.cryptonator.com/api/ticker/";

    public RateDto krbRate(Currency currency) {
        RestTemplate template = new RestTemplate();
        String url = URL.concat(Currency.KRB.name()).concat("-").concat(currency.name());
        CryptonatorTicker ticker = null;

        try {
            ticker = template.getForObject(url, CryptonatorTicker.class);
        } catch (RestClientException e) {
            log.warn(" {} error", e.getMessage());
        }

        if (Objects.nonNull(ticker) && Objects.nonNull(ticker.getTicker()) && ticker.isSuccess()) {
            Ticker t = ticker.getTicker();
            return RateDto.builder()
                    .origin(Currency.valueOf(t.getBase()))
                    .target(Currency.valueOf(t.getTarget()))
                    .rate(t.getPrice())
                    .change(t.getChange().stripTrailingZeros())
                    .build();
        }
        return null;
    }
}
