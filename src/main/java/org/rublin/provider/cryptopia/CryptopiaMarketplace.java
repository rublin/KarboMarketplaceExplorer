package org.rublin.provider.cryptopia;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.controller.v1.AbstractController;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.OrderResponseDto;
import org.rublin.dto.PairDto;
import org.rublin.dto.RateDto;
import org.rublin.model.CryptorpiaPairsEntity;
import org.rublin.provider.AbstractMarketplace;
import org.rublin.provider.Marketplace;
import org.rublin.provider.livecoin.OrderBook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component("ctyptopia")
public class CryptopiaMarketplace extends AbstractMarketplace {

    private static final String CRYPTOPIA_URL = "https://www.cryptopia.co.nz/api/GetMarketOrders/";
    private static final String INTERNAL_SPLIT = "_";

    private final CryptorpiaPairsEntity entity;

    public CryptopiaMarketplace(CryptorpiaPairsEntity entity) {
        this.entity = entity;
        init();
    }

    private void init() {
        String[] pairArray = entity.getPairString().split(SPLIT);
        pairs = Arrays.asList(pairArray);
    }

    @Override
    public List<RateDto> rates() {
        List<RateDto> collect = pairs.stream()
                .map(this::rateByPair)
                .filter(Objects::nonNull)
                .collect(toList());
        return collect;
    }

    @Override
    public TradePlatform name() {
        return TradePlatform.CRYPTOPIA;
    }

    @Override
    public List<OrderResponseDto> trades() {
        List<OrderResponseDto> trades = pairs.stream()
                .map(s -> {
                    Optional<MarketOrders> orderBook = tradeOrders(s);
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

        String url = CRYPTOPIA_URL.concat(supportedPair.get());
        MarketOrders cryptopiaResult = getResponse(url).orElseGet(null);
        if (Objects.nonNull(cryptopiaResult) && cryptopiaResult.getSuccess() && Objects.nonNull(cryptopiaResult.getData())) {
            log.info("{} returns {} buy and {} sell orders",
                    TradePlatform.CRYPTOPIA,
                    cryptopiaResult.getData().getBuy().size(),
                    cryptopiaResult.getData().getSell().size());

            if (pair.isBought()) {
                List<OptimalOrderDto> orders = cryptopiaResult.getData().getSell().stream()
                        .map(trade -> OptimalOrderDto.builder()
                                .marketplace(TradePlatform.CRYPTOPIA.name())
                                .amountToSale(trade.getMTotal())
                                .amountToBuy(trade.getMVolume())
                                .rate(trade.getMPrice())
                                .build())
                        .collect(toList());
                result.addAll(orders);
            } else {
                List<OptimalOrderDto> orders = cryptopiaResult.getData().getBuy().stream()
                        .map(trade -> OptimalOrderDto.builder()
                                .marketplace(TradePlatform.CRYPTOPIA.name())
                                .amountToSale(trade.getMVolume())
                                .amountToBuy(trade.getMTotal())
                                .rate(trade.getMPrice())
                                .build())
                        .collect(toList());
                result.addAll(orders);
            }
        }
        return result;
    }

    private RateDto rateByPair(String pair) {
        String target = pair.substring(4);
        String url = CRYPTOPIA_URL.concat(pair);
        Optional<MarketOrders> response = getResponse(url);
        if (response.isPresent()) {
            MarketOrders marketOrders = response.get();
            Order buyOrder = marketOrders.getData().getBuy().get(0);
            Order sellOrder = marketOrders.getData().getSell().get(0);

            return RateDto.builder()
                    .saleRate(buyOrder.getMPrice())
                    .buyRate(sellOrder.getMPrice())
                    .origin(Currency.KRB)
                    .target(Currency.valueOf(target))
                    .marketplace(TradePlatform.CRYPTOPIA)
                    .build();
        }

        return null;
    }

    private List<OrderResponseDto> createOrderResponse(String pairString, Optional<MarketOrders> orders) {
        List<OrderResponseDto> orderResult = new ArrayList<>();
        if (orders.isPresent()) {
            MarketOrders marketOrders = orders.get();
            log.info("{} returns {} buy and {} sell orders",
                    TradePlatform.CRYPTOPIA,
                    marketOrders.getData().getBuy().size(),
                    marketOrders.getData().getSell().size());
            String[] currencies = pairString.split(INTERNAL_SPLIT);
            orderResult.add(OrderResponseDto.builder()
                    .marketplace(name())
                    .pair(PairDto.builder()
                            .buyCurrency(Currency.valueOf(currencies[0]))
                            .sellCurrency(Currency.valueOf(currencies[1]))
                            .build())
                    .orderList(buyOrders(marketOrders.getData().getSell()))
                    .build());
            orderResult.add(OrderResponseDto.builder()
                    .marketplace(name())
                    .pair(PairDto.builder()
                            .buyCurrency(Currency.valueOf(currencies[1]))
                            .sellCurrency(Currency.valueOf(currencies[0]))
                            .build())
                    .orderList(sellOrders(marketOrders.getData().getBuy()))
                    .build());
        }
        return orderResult;
    }

    private List<OptimalOrderDto> sellOrders(List<Order> orders) {
        return orders.stream()
                .map(trade -> OptimalOrderDto.builder()
                        .marketplace(TradePlatform.CRYPTOPIA.name())
                        .amountToSale(trade.getMVolume())
                        .amountToBuy(trade.getMTotal())
                        .rate(trade.getMPrice())
                        .build())
                .collect(toList());
    }

    private List<OptimalOrderDto> buyOrders(List<Order> orders) {
        return orders.stream()
                .map(trade -> OptimalOrderDto.builder()
                        .marketplace(TradePlatform.CRYPTOPIA.name())
                        .amountToSale(trade.getMTotal())
                        .amountToBuy(trade.getMVolume())
                        .rate(trade.getMPrice())
                        .build())
                .collect(toList());
    }

    private Optional<MarketOrders> tradeOrders(String pairString) {
        String url = CRYPTOPIA_URL.concat(pairString);
        RestTemplate template = new RestTemplate();
        MarketOrders cryptopiaResult = null;
        try {
            log.info("Send {} req", url);
            cryptopiaResult = template.getForObject(url, MarketOrders.class);
        } catch (RestClientException e) {
            log.warn("{} error", e.getMessage());
        }

        return Optional.ofNullable(cryptopiaResult);
    }

    @Deprecated
    private Optional<MarketOrders> getResponse(String url) {
        RestTemplate template = new RestTemplate();
        MarketOrders cryptopiaResult = null;
        try {
            log.info("Send {} req", url);
            cryptopiaResult = template.getForObject(url, MarketOrders.class);
        } catch (RestClientException e) {
            log.warn("{} error", e.getMessage());
        }

        return Optional.ofNullable(cryptopiaResult);
    }
}
