
package org.rublin.provider.cryptopia;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Sell {

    @JsonProperty("Label")
    private String label;
    @JsonProperty("Price")
    private Double price;
    @JsonProperty("Total")
    private Double total;
    @JsonProperty("TradePairId")
    private Long tradePairId;
    @JsonProperty("Volume")
    private Double volume;
}
