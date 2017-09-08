
package ua.com.btctrade;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class CurrentTrade {

    @SerializedName("currency_base")
    private String mCurrencyBase;
    @SerializedName("currency_trade")
    private String mCurrencyTrade;
    @SerializedName("price")
    private String mPrice;
}
