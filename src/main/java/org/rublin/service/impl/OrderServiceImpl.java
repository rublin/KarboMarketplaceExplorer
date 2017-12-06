package org.rublin.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.dto.*;
import org.rublin.provider.FiatRate;
import org.rublin.provider.MarketplaceService;
import org.rublin.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private MarketplaceService marketplaceService;

    @Autowired
    private FiatRate fiatRate;

    @Override
    public List<OptimalOrdersResult> findOptimalOrders(PairDto pair, BigDecimal amount) {
        List<OptimalOrdersResult> results = new LinkedList<>();
        results.add(optimalOrder(pair, amount));
        RateDto rate = fiatRate.rate(PairDto.builder()
                .sellCurrency(Currency.UAH)
                .buyCurrency(Currency.USD)
                .build());
        // Bought order - fiat can be as sell currency
        if (pair.isBought()) {
            if (pair.getSellCurrency() == Currency.UAH) {
                if (Objects.nonNull(rate)) {
                    amount = amount.divide(rate.getBuyRate(), BigDecimal.ROUND_HALF_UP);
                    results.add(optimalOrder(PairDto.builder()
                                    .buyCurrency(pair.getBuyCurrency())
                                    .sellCurrency(Currency.USD)
                                    .build(),
                            amount));
                }
            } else if (pair.getSellCurrency() == Currency.USD) {
                if (Objects.nonNull(rate)) {
                    amount = amount.multiply(rate.getSaleRate());
                    results.add(optimalOrder(PairDto.builder()
                                    .buyCurrency(pair.getBuyCurrency())
                                    .sellCurrency(Currency.UAH)
                                    .build(),
                            amount));
                }
            }

        } else {
            if (pair.getBuyCurrency() == Currency.UAH) {
                if (Objects.nonNull(rate)) {
                    OptimalOrdersResult optimalOrdersResult = optimalOrder(PairDto.builder()
                                    .buyCurrency(Currency.USD)
                                    .sellCurrency(pair.getSellCurrency())
                                    .build(),
                            amount);
                    optimalOrdersResult.setAmountBuy(optimalOrdersResult.getAmountBuy().multiply(rate.getSaleRate()));
                    results.add(optimalOrdersResult);
                }
            } else if (pair.getBuyCurrency() == Currency.USD) {
                if (Objects.nonNull(rate)) {
                    OptimalOrdersResult optimalOrdersResult = optimalOrder(PairDto.builder()
                                    .buyCurrency(Currency.UAH)
                                    .sellCurrency(pair.getSellCurrency())
                                    .build(),
                            amount);
                    optimalOrdersResult.setAmountBuy(optimalOrdersResult.getAmountBuy().divide(rate.getBuyRate(), BigDecimal.ROUND_HALF_UP));
                    results.add(optimalOrdersResult);
                }
            }
        }
        return results;
    }

    private OptimalOrdersResult optimalOrder(PairDto pair, BigDecimal amount) {
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
        BigDecimal sale = BigDecimal.ZERO;
        BigDecimal buy = BigDecimal.ZERO;
        for (OptimalOrderDto optimalOrder : limitedOrders) {
            sale = sale.add(optimalOrder.getAmountToSale());
            buy = buy.add(optimalOrder.getAmountToBuy());
        }

        log.info("Found {} optiomalOrders. Sell {}{}, Buy {}{}",
                limitedOrders.size(),
                sale,
                pair.getSellCurrency(),
                buy,
                pair.getBuyCurrency());
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
