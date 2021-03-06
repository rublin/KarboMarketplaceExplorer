package org.rublin.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.rublin.controller.api.CrexApi;
import org.rublin.controller.api.KunaApi;
import org.rublin.controller.api.RichamsterApi;
import org.rublin.controller.api.TradeogreApi;
import org.rublin.utils.RetrofitBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
//@PropertySource("classpath:telegram.properties")
public class AppConfiguration {

    public static final int TIMEOUT = 40;
    @Value("${provider.tradeogre.url}")
    private String tradeogreUrl;
    @Value("${provider.crex.url}")
    private String crexUrl;

    @Value("${provider.kuna.url}")
    private String kunaUrl;

    @Value("${provider.richamster.url}")
    private String richamsterUrl;

    @Bean
    public TradeogreApi cloudFlareApi() {
        return RetrofitBuilder.build(TradeogreApi.class, tradeogreUrl, TIMEOUT, TimeUnit.SECONDS, mapper());
    }

    @Bean
    public CrexApi crexApi() {
        return RetrofitBuilder.build(CrexApi.class, crexUrl, TIMEOUT, TimeUnit.SECONDS, mapper());
    }

    @Bean
    public KunaApi kunaApi() {
        return RetrofitBuilder.build(KunaApi.class, kunaUrl, TIMEOUT, TimeUnit.SECONDS, mapper());
    }

    @Bean
    public RichamsterApi richamsterApi() {
        return RetrofitBuilder.build(RichamsterApi.class, richamsterUrl, TIMEOUT, TimeUnit.SECONDS, mapper());
    }

    @Bean
    public ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper
                .findAndRegisterModules()
                .registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }
}
