package chapter7;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import chapter5.dto.Order;

@RestController
public class OrderController {

    @GetMapping("/order")
    public Order getOrderDetail() {
        Order order = new Order();
        order.setId(2L);
        order.setContent("from another server");
        return order;
    }
}
