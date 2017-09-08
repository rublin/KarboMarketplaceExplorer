import org.rublin.TradePlatform;
import org.rublin.dto.OrderDto;
import org.rublin.dto.PairDto;

import java.util.List;
import java.util.Map;

public interface Marketplace {
    Map<TradePlatform, List<OrderDto>> tradesByPair(PairDto pair);
}
