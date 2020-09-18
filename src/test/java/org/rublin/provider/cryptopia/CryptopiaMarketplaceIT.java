package org.rublin.provider.cryptopia;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.PairDto;
import org.rublin.provider.AbstractProviderTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
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