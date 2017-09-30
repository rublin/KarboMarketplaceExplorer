package org.rublin.provider.cryptopia;

import org.junit.Test;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;
import org.rublin.provider.AbstractProviderTest;
import org.rublin.provider.Marketplace;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class CryptopiaMarketplaceIT extends AbstractProviderTest {
    Marketplace marketplace = new CryptopiaMarketplace();

    @Test
    public void sellTest() throws Exception {
        PairDto pair = PairDto.builder()
                .sellCurrency(Currency.KRB)
                .buyCurrency(Currency.BTC)
                .build();
        sell(marketplace.tradesByPair(pair), TradePlatform.CRYPTOPIA, pair);
    }

    @Test
    public void buyTest() throws Exception {
        PairDto pair = PairDto.builder()
                .sellCurrency(Currency.BTC)
                .buyCurrency(Currency.KRB)
                .build();
        buy(marketplace.tradesByPair(pair), TradePlatform.CRYPTOPIA, pair);
    }

}