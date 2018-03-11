package org.rublin.provider.com.crex24;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderBook {
    @JsonProperty("SellOrders")
    private List<Order> sellOrders;
    @JsonProperty("BuyOrders")
    private List<Order> buyOrders;
    @JsonProperty("Error")
    private Object error;
}
