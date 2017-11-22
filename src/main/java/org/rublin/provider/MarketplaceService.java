package org.rublin.provider;

import org.rublin.TradePlatform;
import org.rublin.dto.OrderResponseDto;
import org.rublin.dto.PairDto;

import java.util.List;

public interface MarketplaceService {

    /**
     * Create requests to all {@link Marketplace} and save result to cache. Previous result will be deleted
     */
    void createCache();

    /**
     * Get all {@link OrderResponseDto} from cache
     *
     * @return {@link List} of {@link OrderResponseDto}
     */
    List<OrderResponseDto> orders();

    /**
     * All {@link OrderResponseDto} from cache by {@link TradePlatform}
     *
     * @param platform specific
     * @return {@link List} of {@link OrderResponseDto}
     */
    List<OrderResponseDto> orders(TradePlatform platform);

    /**
     * All {@link OrderResponseDto} by {@link PairDto} from cache
     * @param pair specific {@link PairDto}
     * @return {@link List} of {@link OrderResponseDto}
     */
    List<OrderResponseDto> orders(PairDto pair);
}
