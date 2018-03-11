package org.rublin.provider.com.tradeogre;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class TradeorgeOrders {
    private Map<BigDecimal, BigDecimal> buy;
    private Map<BigDecimal, BigDecimal> sell;
}
