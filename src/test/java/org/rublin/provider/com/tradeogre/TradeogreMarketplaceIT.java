package org.rublin.provider.com.tradeogre;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.OrderResponseDto;
import org.rublin.dto.PairDto;
import org.rublin.provider.AbstractProviderTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TradeogreMarketplaceIT extends AbstractProviderTest {

    @Test
    public void orderTest() throws Exception {
        for (OrderResponseDto orderResponse : tradeogreMarketplace.trades()) {
            PairDto pair = orderResponse.getPair();
            List<OptimalOrderDto> orderList = orderResponse.getOrderList();
            if (pair.getSellCurrency() == Currency.KRB)
                sell(orderList, TradePlatform.TRADEOGRE, pair);
            else
                buy(orderList, TradePlatform.TRADEOGRE, pair);
        }
    }
}