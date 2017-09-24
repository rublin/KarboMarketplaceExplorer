package org.rublin.service;

import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.OptimalOrdersResult;
import org.rublin.dto.PairDto;
import org.rublin.provider.Marketplace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    @Qualifier("marketplace")
    private Marketplace marketplace;

    @Override
    public OptimalOrdersResult findOptimalOrders(PairDto pair, BigDecimal amount) {
        List<OptimalOrderDto> orders = marketplace.tradesByPair(pair);
        Comparator<OptimalOrderDto> toSell = new Comparator<OptimalOrderDto>() {
            @Override
            public int compare(OptimalOrderDto o1, OptimalOrderDto o2) {
                return o2.getRate().compareTo(o1.getRate());
            }
        };
        Comparator<OptimalOrderDto> toBuy = new Comparator<OptimalOrderDto>() {
            @Override
            public int compare(OptimalOrderDto o1, OptimalOrderDto o2) {
                return o1.getRate().compareTo(o2.getRate());
            }
        };
        if (pair.getBuyCurrency() == Currency.KRB) {
            Collections.sort(orders, toBuy);
        } else {
            Collections.sort(orders, toSell);
        }
        List<OptimalOrderDto> limitedOrders = new LinkedList<>();
        BigDecimal sellAmount = BigDecimal.ZERO;
        BigDecimal boughtAmount = BigDecimal.ZERO;
        for (OptimalOrderDto order : orders) {
            limitedOrders.add(order);
            if (sellAmount.add(order.getAmountToSale()).compareTo(amount) < 0) {
                sellAmount = sellAmount.add(order.getAmountToSale());
                boughtAmount = boughtAmount.add(order.getAmountToBuy());
            } else {
                BigDecimal remainder = amount.subtract(sellAmount);
                sellAmount = sellAmount.add(remainder);
                boughtAmount = boughtAmount.add(remainder.multiply(order.getRate()));
                break;
            }
        }

        return OptimalOrdersResult.builder()
                .pair(pair)
                .amountSell(sellAmount)
                .amountBuy(boughtAmount)
                .optimalOrders(limitedOrders)
                .build();
    }
}
