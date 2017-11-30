package org.rublin.provider.fiat.privat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrivatRateRsp {

    @JsonProperty("base_ccy")
    private String from;

    @JsonProperty("ccy")
    private String to;

    private BigDecimal buy;

    private BigDecimal sale;
}
