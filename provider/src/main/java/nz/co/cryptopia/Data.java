
package nz.co.cryptopia;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Data {

    @SerializedName("Buy")
    private List<Buy> mBuy;
    @SerializedName("Sell")
    private List<Sell> mSell;

    public List<Buy> getBuy() {
        return mBuy;
    }

    public void setBuy(List<Buy> Buy) {
        mBuy = Buy;
    }

    public List<Sell> getSell() {
        return mSell;
    }

    public void setSell(List<Sell> Sell) {
        mSell = Sell;
    }

}
