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
                if (pair.isBought()) {
                    boughtAmount = boughtAmount.add(remainder.divide(order.getRate(), BigDecimal.ROUND_HALF_UP));
                } else {
                    boughtAmount = boughtAmount.add(remainder.multiply(order.getRate()));
                }
                break;
            }
        }

        return OptimalOrdersResult.builder()
                .pair(pair)
                .amountSell(sellAmount.stripTrailingZeros())
                .amountBuy(boughtAmount.stripTrailingZeros())
                .averageRate(rate(sellAmount, boughtAmount, pair))
                .optimalOrders(limitedOrders)
                .build();
    }

    private BigDecimal rate(BigDecimal sell, BigDecimal buy, PairDto pairDto) {
        if (pairDto.isBought()) {
            return sell.divide(buy, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
        }
        return buy.divide(sell, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
    }
}
