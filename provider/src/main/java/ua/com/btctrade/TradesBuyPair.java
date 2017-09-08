
package ua.com.btctrade;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class TradesBuyPair {

    @SerializedName("list")
    private List<CurrentTrade> list;
    @SerializedName("max_price")
    private String max_price;
    @SerializedName("min_price")
    private String min_price;
    @SerializedName("orders_sum")
    private String orders_sum;

}
