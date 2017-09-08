
package nz.co.cryptopia;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Buy {

    @JsonProperty("Label")
    private String mLabel;
    @JsonProperty("Price")
    private Double mPrice;
    @JsonProperty("Total")
    private Double mTotal;
    @JsonProperty("TradePairId")
    private Long mTradePairId;
    @JsonProperty("Volume")
    private Double mVolume;
}
