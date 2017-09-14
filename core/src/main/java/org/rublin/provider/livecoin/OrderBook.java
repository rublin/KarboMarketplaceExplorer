
package org.rublin.provider.livecoin;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class OrderBook {

    @JsonProperty("timestamp")
    private Long mTimestamp;
    private List<BigDecimal[]> asks;
    private List<BigDecimal[]> bids;

}
