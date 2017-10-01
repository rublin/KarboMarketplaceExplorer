package org.rublin.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.rublin.Currency;
import org.rublin.dto.PairDto;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceImplTest {
    @Spy
    OrderServiceImpl service = new OrderServiceImpl();

    @Test
    public void sortOrders() throws Exception {

    }

    @Test
    public void rate() throws Exception {
        PairDto bought = PairDto.builder().buyCurrency(Currency.KRB).sellCurrency(Currency.BTC).build();
        PairDto sale = PairDto.builder().sellCurrency(Currency.KRB).buyCurrency(Currency.BTC).build();
        BigDecimal rate = service.rate(BigDecimal.valueOf(0.00100).setScale(5), BigDecimal.valueOf(10), bought);
        assertEquals(BigDecimal.valueOf(0.0001).stripTrailingZeros(), rate);
        rate = service.rate(BigDecimal.valueOf(10), BigDecimal.valueOf(0.001).setScale(5), sale);
        assertEquals(BigDecimal.valueOf(0.0001).stripTrailingZeros(), rate);
    }

}