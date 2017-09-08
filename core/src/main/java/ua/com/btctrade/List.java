
package ua.com.btctrade;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class List {

    @SerializedName("currency_base")
    private String mCurrencyBase;
    @SerializedName("currency_trade")
    private String mCurrencyTrade;
    @SerializedName("price")
    private String mPrice;

    public String getCurrencyBase() {
        return mCurrencyBase;
    }

    public void setCurrencyBase(String currencyBase) {
        mCurrencyBase = currencyBase;
    }

    public String getCurrencyTrade() {
        return mCurrencyTrade;
    }

    public void setCurrencyTrade(String currencyTrade) {
        mCurrencyTrade = currencyTrade;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

}
