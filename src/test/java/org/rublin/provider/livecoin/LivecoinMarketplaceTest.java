package org.rublin.provider.livecoin;

import org.junit.Test;
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
    }

    @Test
    public void getAvailablePairs() {
    }

    @Test
    public void trades() {
        List<OrderResponseDto> trades = marketplace.trades();
        assertFalse(trades.isEmpty());
    }
}