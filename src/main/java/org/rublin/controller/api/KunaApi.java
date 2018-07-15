package org.rublin.controller.api;

import org.rublin.provider.io.kuna.KunaOrderBook;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface KunaApi {
    @GET("depth")
    Call<KunaOrderBook> orders(@Query("market") String market);
}
