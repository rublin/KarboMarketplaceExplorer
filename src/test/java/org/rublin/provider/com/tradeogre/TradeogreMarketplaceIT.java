package org.rublin.provider.com.tradeogre;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rublin.provider.AbstractProviderTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TradeogreMarketplaceIT extends AbstractProviderTest {

    @Test
    public void order() throws Exception {
        orderTest(tradeogreMarketplace);
    }
}