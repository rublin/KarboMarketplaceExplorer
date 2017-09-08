import org.rublin.TradePlatform;
import org.rublin.dto.OrderDto;
import org.rublin.dto.PairDto;

import java.util.List;
import java.util.Map;

public class MarketplaceImpl implements Marketplace {
    @Override
    public Map<TradePlatform, List<OrderDto>> tradesByPair(PairDto pair) {
        return null;
    }
}
