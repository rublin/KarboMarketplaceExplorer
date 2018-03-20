package org.rublin.provider.com.crex24;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import static java.lang.String.format;

@Builder
public class Crex24Query {
    /*
    ?request=[PairName=BTC_KRB]
     */
    @JsonProperty("PairName")
    private String pairName;

    @Override
    public String toString() {
        return format("[PairName=%s]", pairName) ;
    }
}
