package org.rublin.provider.cryptopia;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;
import org.rublin.provider.AbstractProviderTest;
import org.rublin.provider.Marketplace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CryptopiaMarketplaceIT extends AbstractProviderTest {

    @Test
    public void sellTest() throws Exception {
        PairDto pair = PairDto.builder()
                .sellCurrency(Currency.KRB)
                .buyCurrency(Currency.BTC)
                .build();
        sell(cryptopiaMarketplace.tradesByPair(pair), TradePlatform.CRYPTOPIA, pair);
    }

    @Test
    public void buyTest() throws Exception {
        PairDto pair = PairDto.builder()
                .sellCurrency(Currency.BTC)
                .buyCurrency(Currency.KRB)
                .build();
        buy(cryptopiaMarketplace.tradesByPair(pair), TradePlatform.CRYPTOPIA, pair);
    }

}