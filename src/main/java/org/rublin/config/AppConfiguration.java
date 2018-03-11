package org.rublin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.rublin.controller.api.TradeogreApi;
import org.rublin.utils.RetrofitBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
//@PropertySource("classpath:telegram.properties")
public class AppConfiguration {

    public static final String TRADEOGRE_URL = "https://tradeogre.com/api/v1/";

    @Bean
    public TradeogreApi cloudFlareApi() {
        return RetrofitBuilder.build(TradeogreApi.class, TRADEOGRE_URL, 40, TimeUnit.SECONDS, mapper());
    }

    @Bean
    public ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper
                .findAndRegisterModules()
                .registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
}
