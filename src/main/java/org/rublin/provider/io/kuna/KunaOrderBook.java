package org.rublin.provider.io.kuna;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class KunaOrderBook {
    private long timestamp;
    private List<BigDecimal[]> asks;
    private List<BigDecimal[]> bids;
}
