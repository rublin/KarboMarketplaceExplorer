package org.rublin.provider.btctrade;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.OrderResponseDto;
import org.rublin.dto.RateDto;
import org.rublin.model.BtcTradePairsEntity;
import org.rublin.provider.Marketplace;

import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public class BtcTradeMarketplaceTest {
    BtcTradeMarketplace marketplace = new BtcTradeMarketplace(pairs());

    private BtcTradePairsEntity pairs() {
        BtcTradePairsEntity entity = new BtcTradePairsEntity();
        entity.setPairString("KRB_UAH,BTC_UAH,BCH_UAH");
        return entity;
    }

    @Test
    public void name() {
        assertEquals(TradePlatform.BTC_TRADE, marketplace.name());
    }

    @Test
    public void trades() {
        assertFalse(marketplace.trades().isEmpty());
    }

    @Test
    public void getAvailablePairs() {
        List<String> availablePairs = marketplace.getAvailablePairs();
        assertFalse(availablePairs.isEmpty());
        assertEquals(3, availablePairs.size());
        assertEquals("KRB_UAH", availablePairs.get(0));
        assertEquals("BTC_UAH", availablePairs.get(1));
        assertEquals("BCH_UAH", availablePairs.get(2));
    }

    @Test
    public void createOrderResponse() {
        List<OrderResponseDto> orderResposeList = marketplace.createOrderResponse("KRB_UAH");
        assertFalse(orderResposeList.isEmpty());
        RateDto rate1 = orderResposeList.get(0).getRate();
        RateDto rate2 = orderResposeList.get(1).getRate();
        assertEquals(rate1, rate2);
        assertTrue(rate1.getSaleRate().doubleValue() <= rate1.getBuyRate().doubleValue());
        for (OrderResponseDto orderResponse : orderResposeList) {
            log.info("Check response with {} pair", orderResponse.getPair());
            assertTrue(checkOrders(orderResponse));
        }
    }

    private boolean checkOrders(OrderResponseDto orderResponse) {
        boolean bought = orderResponse.getPair().isBought();
        OptimalOrderDto first = orderResponse.getOrderList().get(0);
        OptimalOrderDto second = orderResponse.getOrderList().get(1);
        if (bought)
            return first.getRate().compareTo(second.getRate()) < 0;
        return first.getRate().compareTo(second.getRate()) > 0;
    }
}