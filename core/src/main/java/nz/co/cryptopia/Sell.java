
package nz.co.cryptopia;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Sell {

    @SerializedName("Label")
    private String mLabel;
    @SerializedName("Price")
    private Double mPrice;
    @SerializedName("Total")
    private Double mTotal;
    @SerializedName("TradePairId")
    private Long mTradePairId;
    @SerializedName("Volume")
    private Double mVolume;

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String Label) {
        mLabel = Label;
    }

    public Double getPrice() {
        return mPrice;
    }

    public void setPrice(Double Price) {
        mPrice = Price;
    }

    public Double getTotal() {
        return mTotal;
    }

    public void setTotal(Double Total) {
        mTotal = Total;
    }

    public Long getTradePairId() {
        return mTradePairId;
    }

    public void setTradePairId(Long TradePairId) {
        mTradePairId = TradePairId;
    }

    public Double getVolume() {
        return mVolume;
    }

    public void setVolume(Double Volume) {
        mVolume = Volume;
    }

}
