
package ua.com.btctrade;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class TradesBuyPair {

    @SerializedName("list")
    private java.util.List<ua.com.btctrade.List> mList;
    @SerializedName("max_price")
    private String mMaxPrice;
    @SerializedName("min_price")
    private String mMinPrice;
    @SerializedName("orders_sum")
    private String mOrdersSum;

    public java.util.List<ua.com.btctrade.List> getList() {
        return mList;
    }

    public void setList(java.util.List<ua.com.btctrade.List> list) {
        mList = list;
    }

    public String getMaxPrice() {
        return mMaxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        mMaxPrice = maxPrice;
    }

    public String getMinPrice() {
        return mMinPrice;
    }

    public void setMinPrice(String minPrice) {
        mMinPrice = minPrice;
    }

    public String getOrdersSum() {
        return mOrdersSum;
    }

    public void setOrdersSum(String ordersSum) {
        mOrdersSum = ordersSum;
    }

}
