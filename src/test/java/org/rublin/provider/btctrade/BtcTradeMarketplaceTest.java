package org.rublin.provider.btctrade;

import org.junit.Test;
import org.rublin.TradePlatform;
import org.rublin.model.BtcTradePairsEntity;
import org.rublin.provider.Marketplace;

import java.util.List;

import static org.junit.Assert.*;

public class BtcTradeMarketplaceTest {
    Marketplace marketplace = new BtcTradeMarketplace(pairs());

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
}