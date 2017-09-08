package rublin.org.controller;

import org.rublin.dto.PairDto;
import org.rublin.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class RestController {

    @Autowired
    private OrderService orderService;

    public void findOptimalOrders(String sell, String buy, BigDecimal amount) {
        orderService.findOptimalOrders(new PairDto(), amount);
    }
}
