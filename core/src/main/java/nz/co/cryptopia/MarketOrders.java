
package nz.co.cryptopia;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class MarketOrders {

    @SerializedName("Data")
    private nz.co.cryptopia.Data mData;
    @SerializedName("Error")
    private Object mError;
    @SerializedName("Message")
    private Object mMessage;
    @SerializedName("Success")
    private Boolean mSuccess;

    public nz.co.cryptopia.Data getData() {
        return mData;
    }

    public void setData(nz.co.cryptopia.Data Data) {
        mData = Data;
    }

    public Object getError() {
        return mError;
    }

    public void setError(Object Error) {
        mError = Error;
    }

    public Object getMessage() {
        return mMessage;
    }

    public void setMessage(Object Message) {
        mMessage = Message;
    }

    public Boolean getSuccess() {
        return mSuccess;
    }

    public void setSuccess(Boolean Success) {
        mSuccess = Success;
    }

}
