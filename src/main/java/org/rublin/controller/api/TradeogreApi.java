package org.rublin.controller.api;

import org.rublin.provider.com.tradeogre.TradeorgeOrders;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TradeogreApi {

    @GET("orders/{market}")
    Call<TradeorgeOrders> orders(@Path("market") String market);
}
