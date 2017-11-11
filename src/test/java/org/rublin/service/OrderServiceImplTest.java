package org.rublin.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.rublin.Currency;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceImplTest {
    @Spy
    OrderServiceImpl service = new OrderServiceImpl();

    @Test
    public void sortOrders() throws Exception {
        List<OptimalOrderDto> orders = buildOptimalOrders();
        service.sortOrders(orders, buildPair(true));
        assertTrue(orders.get(0).getRate().compareTo(orders.get(1).getRate()) < 0);

        service.sortOrders(orders, buildPair(false));
        assertTrue(orders.get(0).getRate().compareTo(orders.get(1).getRate()) > 0);
    }

    @Test
    public void rate() throws Exception {
        PairDto bought = buildPair(true);
        PairDto sale = buildPair(false);
        BigDecimal rate = service.rate(BigDecimal.valueOf(0.00100).setScale(5), BigDecimal.valueOf(10), bought);
        assertEquals(BigDecimal.valueOf(0.0001).stripTrailingZeros(), rate);
        rate = service.rate(BigDecimal.valueOf(10), BigDecimal.valueOf(0.001).setScale(5), sale);
        assertEquals(BigDecimal.valueOf(0.0001).stripTrailingZeros(), rate);
    }
    
    private PairDto buildPair(boolean bought) {
        if (bought) {
            return PairDto.builder().buyCurrency(Currency.KRB).sellCurrency(Currency.BTC).build();
        }
        return PairDto.builder().sellCurrency(Currency.KRB).buyCurrency(Currency.BTC).build();
    }
    
    private List<OptimalOrderDto> buildOptimalOrders() {
        List<OptimalOrderDto> result = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            result.add(OptimalOrderDto.builder().rate(random()).build());
        }

        return result;
    }

    private BigDecimal random() {
        return BigDecimal.valueOf(Math.random());
    }
}