
package org.rublin.provider.cryptopia;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Buy {

    @JsonProperty("Label")
    private String mLabel;
    @JsonProperty("Price")
    private BigDecimal mPrice;
    @JsonProperty("Total")
    private BigDecimal mTotal;
    @JsonProperty("TradePairId")
    private Long mTradePairId;
    @JsonProperty("Volume")
    private BigDecimal mVolume;
}
