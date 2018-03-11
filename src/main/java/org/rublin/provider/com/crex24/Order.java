package org.rublin.provider.com.crex24;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {
    @JsonProperty("Id")
    private int id;
    @JsonProperty("PairName")
    private String pairName;
    @JsonProperty("IsSell")
    private boolean sell;
    @JsonProperty("CoinPrice")
    private BigDecimal price;
    @JsonProperty("CoinCount")
    private BigDecimal amount;
    @JsonProperty("AccumulateMoneyVolume")
    private BigDecimal moneyVolume;
    @JsonProperty("AccumulateMoneyCount")
    private BigDecimal moneyCount;
    @JsonProperty("IsCloseRequired")
    private boolean closeRequired;
    @JsonProperty("Type")
    private int type;
    @JsonProperty("TypeProcessing")
    private int typeProcessing;
    @JsonProperty("StopPrice")
    private BigDecimal stopPrice;
}
