package com.demo.orders.service.order;

import com.demo.orders.dto.CreateOrderDto;
import com.demo.orders.dto.OrderDto;

import java.util.List;

public interface OrderService {

    OrderDto placeOrder(CreateOrderDto createOrderDto);
    List<OrderDto> getOrdersInPeriod(Long timeFrom, Long timeTo);

    OrderDto getOrder(Long id);
    List<OrderDto> getAllOrders();
}
