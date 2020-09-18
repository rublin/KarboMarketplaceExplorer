package org.rublin.provider.cryptopia;

import org.junit.Ignore;
import org.junit.Test;
import org.rublin.TradePlatform;
import org.rublin.model.CryptorpiaPairsEntity;
import org.rublin.provider.Marketplace;

import java.util.List;

import static org.junit.Assert.*;

@Ignore
public class CryptopiaMarketplaceTest {
    Marketplace marketplace = new CryptopiaMarketplace(pairs());

    private CryptorpiaPairsEntity pairs() {
        CryptorpiaPairsEntity entity = new CryptorpiaPairsEntity();
        entity.setPairString("KRB_BTC");
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
        assertEquals(1, availablePairs.size());
        assertEquals("KRB_BTC", availablePairs.get(0));
    }
}