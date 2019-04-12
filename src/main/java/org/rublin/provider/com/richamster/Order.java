package org.rublin.provider.com.richamster;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {
    private BigDecimal volume;

    @JsonProperty("unit_price")
    private BigDecimal price;

    private BigDecimal sum;

    private String side;

    private String pair;
}
