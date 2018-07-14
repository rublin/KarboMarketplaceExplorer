package org.rublin.provider.com.tradeogre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeorgeOrders {
    private Map<BigDecimal, BigDecimal> buy;
    private Map<BigDecimal, BigDecimal> sell;
}
