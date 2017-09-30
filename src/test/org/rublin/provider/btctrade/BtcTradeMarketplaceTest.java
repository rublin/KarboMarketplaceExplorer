package org.rublin.provider.btctrade;

import org.junit.Test;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;
import org.rublin.provider.AbstractProviderTest;
import org.rublin.provider.Marketplace;
import org.rublin.provider.cryptopia.CryptopiaMarketplace;

import java.util.List;

public class BtcTradeMarketplaceTest extends AbstractProviderTest {
    Marketplace marketplace = new BtcTradeMarketplace();

    @Test
    public void sellTest() throws Exception {
        PairDto pair = PairDto.builder()
                .sellCurrency(Currency.KRB)
                .buyCurrency(Currency.UAH)
                .build();
        sell(marketplace.tradesByPair(pair), TradePlatform.BTC_TRADE, pair);
    }

    @Test
    public void buyTest() throws Exception {
        PairDto pair = PairDto.builder()
                .sellCurrency(Currency.UAH)
                .buyCurrency(Currency.KRB)
                .build();
        buy(marketplace.tradesByPair(pair), TradePlatform.BTC_TRADE, pair);
    }
}