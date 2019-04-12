package org.rublin.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

@Slf4j
@Component
public class CallExecutor {
    public  <T> T execute(Call call) {
        log.info(call.request().toString());
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                return (T) response.body();
            } else {
                log.error("error body {}", response.raw().toString());
            }
        } catch (IOException e) {
            log.error("error execute api", e);
        }
        throw new RuntimeException("error execute unsuccessful");
    }

    public Response executeAndGetResponse(Call call) {
        log.info(call.request().tag().toString());
        try {
            return call.execute();
        } catch (IOException e) {
            log.error("error execute api", e);
            throw new RuntimeException("error execute is unsuccessful");
        }
    }
}
