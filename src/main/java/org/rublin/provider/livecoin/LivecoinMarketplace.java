package org.rublin.provider.livecoin;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.OrderResponseDto;
import org.rublin.dto.PairDto;
import org.rublin.dto.RateDto;
import org.rublin.model.LivecoinPairsEntity;
import org.rublin.provider.AbstractMarketplace;
import org.rublin.provider.Marketplace;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component("livecoin")
public class LivecoinMarketplace extends AbstractMarketplace {

    private static final String LIVECOIN = "https://api.livecoin.net/exchange/order_book?currencyPair=";
    private static final String INTERNAL_SPLIT = "/";

    private final LivecoinPairsEntity entity;

    public LivecoinMarketplace(LivecoinPairsEntity entity) {
        this.entity = entity;
        init();
    }

    private void init() {
        String[] pairArray = entity.getPairString().split(SPLIT);
        pairs = Arrays.asList(pairArray);
    }

    @Override
    public TradePlatform name() {
        return TradePlatform.LIVECOIN;
    }

    @Override
    public List<RateDto> rates() {
        return pairs.stream()
                .map(this::rateByPair)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    @Override
    public List<OrderResponseDto> trades() {
        List<OrderResponseDto> trades = pairs.stream()
                .map(s -> {
                    Optional<OrderBook> orderBook = tradeOrders(s);
                    return createOrderResponse(s, orderBook);
                })
                .flatMap(List::stream)
                .collect(toList());
        return trades;
    }

    @Override
    public List<OptimalOrderDto> tradesByPair(PairDto pair) {
        List<OptimalOrderDto> result = new ArrayList<>();
        Currency buy = pair.getBuyCurrency();
        Currency sell = pair.getSellCurrency();

        Optional<String> supportedPair = pairs.stream()
                .filter(s -> s.contains(buy.name()) && s.contains(sell.name()))
                .findFirst();
        if (!supportedPair.isPresent()) {
            return result;
        }
        String url = LIVECOIN.concat(supportedPair.get());

        OrderBook orderBook = getResponse(url).orElseGet(null);

        if (orderBook != null) {
            log.info("{} returns {} buy and {} sell orders",
                    TradePlatform.LIVECOIN,
                    orderBook.getAsks().size(),
                    orderBook.getBids().size());

            if (pair.isBought()) {
                List<OptimalOrderDto> orders = orderBook.getAsks().stream()
                        .map(trade -> OptimalOrderDto.builder()
                                .marketplace(TradePlatform.LIVECOIN.name())
                                .amountToSale(trade[1].multiply(trade[0]))
                                .amountToBuy(trade[1])
                                .rate(trade[0])
                                .build())
                        .collect(toList());
                result.addAll(orders);
            } else {
                List<OptimalOrderDto> orders = orderBook.getBids().stream()
                        .map(trade -> OptimalOrderDto.builder()
                                .marketplace(TradePlatform.LIVECOIN.name())
                                .amountToSale(trade[1])
                                .amountToBuy(trade[1].multiply(trade[0]))
                                .rate(trade[0])
                                .build())
                        .collect(toList());
                result.addAll(orders);
            }
        }
        return result;
    }

    private RateDto rateByPair(String pair) {
        String target = pair.substring(4);
        String url = LIVECOIN.concat(pair);
        Optional<OrderBook> response = getResponse(url);
        if (response.isPresent()) {
            BigDecimal buy = response.get().getAsks().get(0)[0];
            BigDecimal sell = response.get().getBids().get(0)[0];
            return RateDto.builder()
                    .saleRate(sell)
                    .buyRate(buy)
                    .origin(Currency.KRB)
                    .target(Currency.valueOf(target))
                    .marketplace(TradePlatform.LIVECOIN)
                    .build();
        }
        return null;
    }

    private Optional<OrderBook> tradeOrders(String pairString) {
        String url = LIVECOIN.concat(pairString);
        RestTemplate template = new RestTemplate();
        OrderBook livecoinResults = null;
        try {
            log.info("Send {} req", url);
            livecoinResults = template.getForObject(url, OrderBook.class);
        } catch (Throwable e) {
            log.warn("{} error", e.getMessage());
        }
        return Optional.ofNullable(livecoinResults);
    }

    @Deprecated
    private Optional<OrderBook> getResponse(String url) {
        RestTemplate template = new RestTemplate();
        OrderBook livecoinResults = null;
        try {
            log.info("Send {} req", url);
            livecoinResults = template.getForObject(url, OrderBook.class);
        } catch (Throwable e) {
            log.warn("{} error", e.getMessage());
        }
        return Optional.ofNullable(livecoinResults);
    }

    private List<OrderResponseDto> createOrderResponse(String pairString, Optional<OrderBook> orderBookOptional) {
        List<OrderResponseDto> orderResult = new ArrayList<>();
        if (orderBookOptional.isPresent()) {
            OrderBook orderBook = orderBookOptional.get();
            log.info("{} returns {} buy and {} sell orders",
                    name(),
                    orderBook.getAsks().size(),
                    orderBook.getBids().size());
            String[] currencies = pairString.split(INTERNAL_SPLIT);
            orderResult.add(OrderResponseDto.builder()
                    .marketplace(name())
                    .pair(PairDto.builder()
                            .buyCurrency(Currency.valueOf(currencies[0]))
                            .sellCurrency(Currency.valueOf(currencies[1]))
                            .build())
                    .orderList(buyOrders(orderBook.getAsks()))
                    .build());
            orderResult.add(OrderResponseDto.builder()
                    .marketplace(name())
                    .pair(PairDto.builder()
                            .buyCurrency(Currency.valueOf(currencies[1]))
                            .sellCurrency(Currency.valueOf(currencies[0]))
                            .build())
                    .orderList(sellOrders(orderBook.getBids()))
                    .build());
        }
        return orderResult;
    }

    private List<OptimalOrderDto> sellOrders(List<BigDecimal[]> orders) {
        return orders.stream()
                .map(trade -> OptimalOrderDto.builder()
                        .marketplace(TradePlatform.LIVECOIN.name())
                        .amountToSale(trade[1])
                        .amountToBuy(trade[1].multiply(trade[0]))
                        .rate(trade[0])
                        .build())
                .collect(toList());
    }

    private List<OptimalOrderDto> buyOrders(List<BigDecimal[]> orders) {
        return orders.stream()
                .map(trade -> OptimalOrderDto.builder()
                        .marketplace(TradePlatform.LIVECOIN.name())
                        .amountToSale(trade[1].multiply(trade[0]))
                        .amountToBuy(trade[1])
                        .rate(trade[0])
                        .build())
                .collect(toList());
    }
}

