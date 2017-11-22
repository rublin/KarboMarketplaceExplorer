package org.rublin.provider.cryptopia;

import org.junit.Test;
import org.rublin.TradePlatform;
import org.rublin.model.CryptorpiaPairsEntity;
import org.rublin.provider.Marketplace;

import java.util.List;

import static org.junit.Assert.*;

public class CryptopiaMarketplaceTest {
    Marketplace marketplace = new CryptopiaMarketplace(pairs());

    private CryptorpiaPairsEntity pairs() {
        CryptorpiaPairsEntity entity = new CryptorpiaPairsEntity();
        entity.setPairString("KRB_BTC,KRB_LTC,KRB_DOGE");
        return entity;
    }

    @Test
    public void name() {
        assertEquals(TradePlatform.CRYPTOPIA, marketplace.name());
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
        assertEquals("KRB_BTC", availablePairs.get(0));
        assertEquals("KRB_LTC", availablePairs.get(1));
        assertEquals("KRB_DOGE", availablePairs.get(2));
    }
}