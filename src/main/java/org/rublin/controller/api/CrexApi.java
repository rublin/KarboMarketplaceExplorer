package org.rublin.controller.api;

import org.rublin.provider.com.crex24.Crex24Query;
import org.rublin.provider.com.crex24.OrderBook;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CrexApi {

    @GET("ReturnOrderBook")
    Call<OrderBook> orders(@Query("request") Crex24Query request);
}
