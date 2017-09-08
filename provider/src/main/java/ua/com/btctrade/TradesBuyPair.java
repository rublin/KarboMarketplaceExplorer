
package ua.com.btctrade;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class TradesBuyPair {

    @JsonProperty("list")
    private List<CurrentTrade> trades;
    @JsonProperty("max_price")
    private BigDecimal maxPrice;
    @JsonProperty("min_price")
    private BigDecimal minPrice;
    @JsonProperty("orders_sum")
    private String ordersSum;

}
