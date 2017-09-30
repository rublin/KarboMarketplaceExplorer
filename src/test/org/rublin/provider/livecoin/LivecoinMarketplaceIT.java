package org.rublin.provider.livecoin;

import org.junit.Test;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;
import org.rublin.provider.AbstractProviderTest;
import org.rublin.provider.Marketplace;

import java.util.List;

public class LivecoinMarketplaceIT extends AbstractProviderTest {
    Marketplace marketplace = new LivecoinMarketplace();

    @Test
    public void sellTest() throws Exception {
        PairDto pair = PairDto.builder()
                .sellCurrency(Currency.KRB)
                .buyCurrency(Currency.BTC)
                .build();
        sell(marketplace.tradesByPair(pair), TradePlatform.LIVECOIN, pair);
    }

    @Test
    public void buyTest() throws Exception {
        PairDto pair = PairDto.builder()
                .sellCurrency(Currency.BTC)
                .buyCurrency(Currency.KRB)
                .build();
        buy(marketplace.tradesByPair(pair), TradePlatform.LIVECOIN, pair);
    }
}