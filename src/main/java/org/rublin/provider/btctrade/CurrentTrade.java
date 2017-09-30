
package org.rublin.provider.btctrade;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class CurrentTrade {

    @JsonProperty("currency_base")
    private BigDecimal currencyBase;
    @JsonProperty("currency_trade")
    private BigDecimal currencyTrade;
    @JsonProperty("price")
    private BigDecimal price;
}
