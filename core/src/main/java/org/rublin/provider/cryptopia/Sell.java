
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
public class Sell {

    @JsonProperty("Label")
    private String label;
    @JsonProperty("Price")
    private BigDecimal price;
    @JsonProperty("Total")
    private BigDecimal total;
    @JsonProperty("TradePairId")
    private Long tradePairId;
    @JsonProperty("Volume")
    private BigDecimal volume;
}
