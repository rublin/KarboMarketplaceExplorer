
package ua.com.btctrade;

import javax.annotation.Generated;
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

    @SerializedName("list")
    private List<CurrentTrade> list;
    @SerializedName("max_price")
    private BigDecimal max_price;
    @SerializedName("min_price")
    private BigDecimal min_price;
    @SerializedName("orders_sum")
    private String orders_sum;

}
