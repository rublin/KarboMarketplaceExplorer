package org.rublin.provider.com.richamster;

import lombok.Data;

import java.util.List;

@Data
public class RichamsterOrderBook {
    private List<Order> buying;
    private List<Order> selling;
}
