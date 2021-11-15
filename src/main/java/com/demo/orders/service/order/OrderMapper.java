package com.demo.orders.service.order;

import com.demo.orders.dto.CreateOrderDto;
import com.demo.orders.dto.OrderDto;
import com.demo.orders.model.Order;

public interface OrderMapper {

    OrderDto mapToDto(Order order);
    Order mapCreateDtoToEntity(CreateOrderDto createOrderDto);
}
