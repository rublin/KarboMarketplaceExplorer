
package ua.com.btctrade;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class CurrentTrade {

    @SerializedName("currency_base")
    private BigDecimal currency_base;
    @SerializedName("currency_trade")
    private BigDecimal currency_trade;
    @SerializedName("price")
    private BigDecimal price;
}
