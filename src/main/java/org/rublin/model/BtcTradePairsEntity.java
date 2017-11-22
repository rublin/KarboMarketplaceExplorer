package org.rublin.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class BtcTradePairsEntity {

    @Value("${provider.btc-trade.pair}")
    private String pairString;
}
