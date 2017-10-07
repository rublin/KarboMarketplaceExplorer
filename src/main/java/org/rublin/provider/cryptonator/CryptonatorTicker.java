
package org.rublin.provider.cryptonator;

import lombok.Data;

@Data
public class CryptonatorTicker {

    private String error;
    private boolean success;
    private Ticker ticker;
    private Long timestamp;
}
