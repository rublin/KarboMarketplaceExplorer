package org.rublin.controller.api;

import org.rublin.provider.com.crex24.OrderBook;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CrexApi {

    @GET("ReturnOrderBook?request=[PairName=BTC_KRB]")
    Call<OrderBook> orders();
}
