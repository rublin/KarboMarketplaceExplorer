package org.rublin.provider.livecoin;

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

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class LivecoinMarketplaceIT extends AbstractProviderTest {

    @Test
    public void sellTest() throws Exception {
        PairDto pair = PairDto.builder()
                .sellCurrency(Currency.KRB)
                .buyCurrency(Currency.BTC)
                .build();
        sell(livecoinMarketplace.tradesByPair(pair), TradePlatform.LIVECOIN, pair);
    }

    @Test
    public void buyTest() throws Exception {
        PairDto pair = PairDto.builder()
                .sellCurrency(Currency.BTC)
                .buyCurrency(Currency.KRB)
                .build();
        buy(livecoinMarketplace.tradesByPair(pair), TradePlatform.LIVECOIN, pair);
    }
}