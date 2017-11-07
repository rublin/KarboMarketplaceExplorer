package org.rublin.provider;

import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AbstractProviderTest {

    public void sell(List<OptimalOrderDto> orders, TradePlatform platform, PairDto pair) {
        assertTrue(Objects.nonNull(orders));
        assertTrue(orders.size() > 10);
        OptimalOrderDto firstOrder = orders.get(0);
        if (pair.getBuyCurrency() == Currency.UAH) {
            assertTrue(firstOrder.getAmountToSale()
                    .compareTo(firstOrder.getAmountToBuy()) < 0);
            assertEquals(firstOrder.getRate().stripTrailingZeros().doubleValue(), firstOrder.getAmountToBuy()
                    .divide(firstOrder.getAmountToSale(),
                            2,
                            BigDecimal.ROUND_HALF_UP).stripTrailingZeros().doubleValue(), 2);
        } else {
            assertTrue(firstOrder.getAmountToSale()
                    .compareTo(firstOrder.getAmountToBuy()) > 0);
            assertEquals(firstOrder.getRate().stripTrailingZeros(), firstOrder.getAmountToBuy()
                    .divide(firstOrder.getAmountToSale(),
                            BigDecimal.ROUND_HALF_UP).stripTrailingZeros());
        }
        assertEquals(firstOrder.getMarketplace(), platform.name());

    }

    public void buy(List<OptimalOrderDto> orders, TradePlatform platform, PairDto pair) {
        assertTrue(Objects.nonNull(orders));
        assertTrue(orders.size() > 10);
        OptimalOrderDto firstOrder = orders.get(0);
        if (pair.getSellCurrency() == Currency.UAH) {
            assertTrue(firstOrder.getAmountToBuy()
                    .compareTo(firstOrder.getAmountToSale()) < 0);
            assertEquals(
                    firstOrder.getRate().stripTrailingZeros().doubleValue(),
                    firstOrder.getAmountToSale()
                            .divide(firstOrder.getAmountToBuy(),
                                    2,
                                    BigDecimal.ROUND_HALF_UP).stripTrailingZeros().doubleValue(),
                    0.01
            );
        } else {
            assertTrue(firstOrder.getAmountToBuy()
                    .compareTo(firstOrder.getAmountToSale()) > 0);
            assertEquals(
                    firstOrder.getRate().stripTrailingZeros().doubleValue(),
                    firstOrder.getAmountToSale()
                            .divide(firstOrder.getAmountToBuy(),
                                    BigDecimal.ROUND_HALF_UP).stripTrailingZeros().doubleValue(),
                    0.01);
        }
        assertEquals(firstOrder.getMarketplace(), platform.name());

    }
}
