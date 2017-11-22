package org.rublin.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.OptimalOrdersResult;
import org.rublin.dto.OrderResponseDto;
import org.rublin.dto.PairDto;
import org.rublin.provider.MarketplaceService;
import org.rublin.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private MarketplaceService marketplaceService;

    @Override
    public OptimalOrdersResult findOptimalOrders(PairDto pair, BigDecimal amount) {
        List<OptimalOrderDto> orders = marketplaceService.orders(pair).stream()
                .map(OrderResponseDto::getOrderList)
                .flatMap(List::stream)
                .collect(toList());

        sortOrders(orders, pair);
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

    protected void sortOrders(List<OptimalOrderDto> orders, PairDto pair) {
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
        if (pair.isBought()) {
            Collections.sort(orders, toBuy);
        } else {
            Collections.sort(orders, toSell);
        }
    }

    protected BigDecimal rate(BigDecimal sell, BigDecimal buy, PairDto pairDto) {
        if (pairDto.isBought()) {
            BigDecimal divide = sell.divide(buy, BigDecimal.ROUND_HALF_UP);
            return divide.stripTrailingZeros();
        }
        return buy.divide(sell, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
    }
}
