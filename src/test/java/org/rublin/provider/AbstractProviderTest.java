package org.rublin.provider;

import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AbstractProviderTest {
    @Autowired
    @Qualifier("btc")
    protected Marketplace btcTradeMarketplace;

    @Autowired
    @Qualifier("ctyptopia")
    protected Marketplace cryptopiaMarketplace;

    @Autowired
    @Qualifier("livecoin")
    protected Marketplace livecoinMarketplace;

    @Autowired
    @Qualifier("tradeogre")
    protected Marketplace tradeogreMarketplace;

    public void sell(List<OptimalOrderDto> orders, TradePlatform platform, PairDto pair) {
        assertTrue(Objects.nonNull(orders));
        assertTrue(orders.size() > 5);
        OptimalOrderDto firstOrder = orders.get(0);
        OptimalOrderDto secondOrder = orders.get(1);
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
        assertTrue(firstOrder.getRate().compareTo(secondOrder.getRate()) > 0);
        assertEquals(firstOrder.getMarketplace(), platform.name());

    }

    public void buy(List<OptimalOrderDto> orders, TradePlatform platform, PairDto pair) {
        assertTrue(Objects.nonNull(orders));
        assertTrue(orders.size() > 10);
        OptimalOrderDto firstOrder = orders.get(0);
        OptimalOrderDto secondOrder = orders.get(1);
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
        assertTrue(firstOrder.getRate().compareTo(secondOrder.getRate()) < 0);
        assertEquals(firstOrder.getMarketplace(), platform.name());

    }
}
