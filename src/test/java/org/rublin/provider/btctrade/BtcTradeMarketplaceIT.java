package org.rublin.provider.btctrade;

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
public class BtcTradeMarketplaceIT extends AbstractProviderTest {

    @Test
    public void sellTest() throws Exception {
        PairDto pair = PairDto.builder()
                .sellCurrency(Currency.KRB)
                .buyCurrency(Currency.UAH)
                .build();
        sell(btcTradeMarketplace.tradesByPair(pair), TradePlatform.BTC_TRADE, pair);
    }

    @Test
    public void buyTest() throws Exception {
        PairDto pair = PairDto.builder()
                .sellCurrency(Currency.UAH)
                .buyCurrency(Currency.KRB)
                .build();
        buy(btcTradeMarketplace.tradesByPair(pair), TradePlatform.BTC_TRADE, pair);
    }
}