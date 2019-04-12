package org.rublin.controller.api;

import org.rublin.provider.com.richamster.RichamsterOrderBook;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RichamsterApi {
    @GET("exchange/order-book")
    Call<RichamsterOrderBook> orders(@Query("pair") String pair);
}
