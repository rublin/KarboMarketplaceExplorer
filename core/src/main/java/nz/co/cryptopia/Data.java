
package nz.co.cryptopia;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Data {

    @SerializedName("Buy")
    private List<nz.co.cryptopia.Buy> mBuy;
    @SerializedName("Sell")
    private List<nz.co.cryptopia.Sell> mSell;

    public List<nz.co.cryptopia.Buy> getBuy() {
        return mBuy;
    }

    public void setBuy(List<nz.co.cryptopia.Buy> Buy) {
        mBuy = Buy;
    }

    public List<nz.co.cryptopia.Sell> getSell() {
        return mSell;
    }

    public void setSell(List<nz.co.cryptopia.Sell> Sell) {
        mSell = Sell;
    }

}
