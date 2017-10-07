
package org.rublin.provider.cryptonator;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Ticker {

    private String base;
    private BigDecimal change;
    private BigDecimal price;
    private String target;
    private String volume;
}
