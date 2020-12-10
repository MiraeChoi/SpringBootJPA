package jpabook.jpashop2.api;

import jpabook.jpashop2.domain.Order;
import jpabook.jpashop2.repository.OrderRepository;
import jpabook.jpashop2.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//XToOne(ManyToOne, OneToOne)
//Order
//Order -> Member
//Order -> Delivery
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //LAZY 강제 초기화(.getName()부터 쿼리가 나감
            order.getDelivery().getAddress(); //LAZY 강제 초기화2
        }

        return all;
    }
}
