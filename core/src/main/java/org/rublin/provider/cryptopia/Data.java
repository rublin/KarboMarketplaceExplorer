
package org.rublin.provider.cryptopia;

import java.util.List;
import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Data {

    @JsonProperty("Buy")
    private List<Order> buy;
    @JsonProperty("Sell")
    private List<Order> sell;
}
