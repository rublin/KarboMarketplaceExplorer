package org.rublin.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitBuilder {
    public static <V> V build(Class<V> clazz, final String endpoint, long readTimeout, TimeUnit readTimeoutUnit, final ObjectMapper mapper) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(readTimeout, readTimeoutUnit)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(endpoint)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .client(httpClient)
                .build();

        return restAdapter.create(clazz);
    }

}
