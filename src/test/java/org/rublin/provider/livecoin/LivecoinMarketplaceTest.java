package org.rublin.provider.livecoin;

import org.junit.Test;
import org.rublin.TradePlatform;
import org.rublin.dto.OrderResponseDto;
import org.rublin.model.LivecoinPairsEntity;
import org.rublin.provider.Marketplace;

import java.util.List;

import static org.junit.Assert.*;

public class LivecoinMarketplaceTest {
    Marketplace marketplace = new LivecoinMarketplace(pairs());

    private LivecoinPairsEntity pairs() {
        LivecoinPairsEntity entity = new LivecoinPairsEntity();
        entity.setPairString("KRB/BTC,KRB/RUR,KRB/USD");
        return entity;
    }

    @Test
    public void name() {
        TradePlatform name = marketplace.name();
        assertEquals(TradePlatform.LIVECOIN, name);
    }

    @Test
    public void getAvailablePairs() {
        List<String> availablePairs = marketplace.getAvailablePairs();
        assertFalse(availablePairs.isEmpty());
        assertEquals(3, availablePairs.size());
        assertEquals("KRB/BTC", availablePairs.get(0));
        assertEquals("KRB/RUR", availablePairs.get(1));
        assertEquals("KRB/USD", availablePairs.get(2));
    }

    @Test
    public void trades() {
        List<OrderResponseDto> trades = marketplace.trades();
        assertFalse(trades.isEmpty());
    }
}