package org.rublin.provider.btctrade;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rublin.AppConfiguration;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;
import org.rublin.provider.AbstractProviderTest;
import org.rublin.provider.Marketplace;
import org.rublin.provider.cryptopia.CryptopiaMarketplace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class BtcTradeMarketplaceTest extends AbstractProviderTest {

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