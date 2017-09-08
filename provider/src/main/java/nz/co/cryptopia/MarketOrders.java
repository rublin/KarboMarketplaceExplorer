
package nz.co.cryptopia;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class MarketOrders {

    @JsonProperty("Data")
    private Data mData;
    @JsonProperty("Error")
    private Object mError;
    @JsonProperty("Message")
    private Object mMessage;
    @JsonProperty("Success")
    private Boolean mSuccess;

    public Data getData() {
        return mData;
    }

    public void setData(Data Data) {
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
