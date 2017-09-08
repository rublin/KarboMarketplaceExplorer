
package nz.co.cryptopia;

import java.util.List;
import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Data {

    @JsonProperty("Buy")
    private List<Buy> mBuy;
    @JsonProperty("Sell")
    private List<Sell> mSell;
}
