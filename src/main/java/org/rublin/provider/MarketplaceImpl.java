package org.rublin.provider;

import lombok.extern.slf4j.Slf4j;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.PairDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component("marketplace")
public class MarketplaceImpl implements Marketplace {

    @Autowired
    @Qualifier("btc")
    private Marketplace btcTradeMarketplace;

    @Autowired
    @Qualifier("ctyptopia")
    private Marketplace cryptopiaMarketplace;

    @Autowired
    @Qualifier("livecoin")
    private Marketplace livecoinMarketplace;

    @Override
    public List<OptimalOrderDto> tradesByPair(PairDto pair) {
        List<Marketplace> marketplaces = Arrays.asList(cryptopiaMarketplace, livecoinMarketplace, btcTradeMarketplace);
        List<OptimalOrderDto> result = marketplaces.parallelStream()
                .map(marketplace -> marketplace.tradesByPair(pair))
                .flatMap(List::stream)
                .collect(toList());
        return result;
    }

    /*private T requestOrders(String url, T cl) {
        RestTemplate template = new RestTemplate();
        try {
            OrderBook livecoinResult = template.getForObject(url, );
            log.debug("{) results from livecoin", livecoinResult.getAsks().size());
            livecoinResult.getAsks().forEach(ask -> log.debug("Asc {} ", ask));
        } catch (RestClientException e) {
            log.warn("Error from provider: {}", e.getMessage());
        }
        return null;
    }*/
}
