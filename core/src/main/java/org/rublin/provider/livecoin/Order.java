package org.rublin.provider.livecoin;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {
    private BigDecimal[] order;

    public BigDecimal getPrice() {
        return order[0];
    }

    public BigDecimal getAmount() {
        return order[1];
    }
}
