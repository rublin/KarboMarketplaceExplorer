package org.rublin.provider.livecoin;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;
import org.rublin.provider.Marketplace;
import org.rublin.provider.cryptopia.MarketOrders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component("livecoin")
public class LivecoinMarketplace implements Marketplace {

    public static final String LIVECOIN = "https://api.livecoin.net/exchange/order_book?currencyPair=";

    @Value("${provider.livecoin.pair}")
    private String pairString;

    private List<String> livecoinPair;

    @PostConstruct
    private void init() {
        String[] pairArray = pairString.split(",");
        livecoinPair = Arrays.asList(pairArray);
    }

    @Override
    public List<OptimalOrderDto> tradesByPair(PairDto pair) {
        List<OptimalOrderDto> result = new ArrayList<>();
        RestTemplate template = new RestTemplate();
        Currency buy = pair.getBuyCurrency();
        Currency sell = pair.getSellCurrency();

        Optional<String> supportedPair = livecoinPair.stream()
                .filter(s -> s.contains(buy.name()) && s.contains(sell.name()))
                .findFirst();
        if (!supportedPair.isPresent()) {
            return result;
        }
        String url = LIVECOIN.concat(supportedPair.get());

        OrderBook livecoinResults = null;
        try {
            log.info("Send {} req", url);
            livecoinResults = template.getForObject(url, OrderBook.class);
        } catch (Throwable e) {
            log.warn("{} error", e.getMessage());
        }

        if (livecoinResults != null) {
            log.info("{} returns {} buy and {} sell orders",
                    TradePlatform.LIVECOIN,
                    livecoinResults.getAsks().size(),
                    livecoinResults.getBids().size());

            if (pair.isBought()) {
                List<OptimalOrderDto> orders = livecoinResults.getAsks().stream()
                        .map(trade -> OptimalOrderDto.builder()
                                .marketplace(TradePlatform.LIVECOIN.name())
                                .amountToSale(trade[1].multiply(trade[0]))
                                .amountToBuy(trade[1])
                                .rate(trade[0])
                                .build())
                        .collect(toList());
                result.addAll(orders);
            } else {
                List<OptimalOrderDto> orders = livecoinResults.getBids().stream()
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
}

