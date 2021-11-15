package com.demo.orders.service.order.impl;

import com.demo.orders.dto.CreateOrderDto;
import com.demo.orders.dto.OrderDto;
import com.demo.orders.dto.OrderItemDto;
import com.demo.orders.model.Order;
import com.demo.orders.model.OrderItem;
import com.demo.orders.service.order.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class OrderMapperImpl implements OrderMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void init () {
        modelMapper.addMappings(new PropertyMap<OrderItem, OrderItemDto>() {
            @Override
            protected void configure() {
                map().setName(source.getProduct().getName());
            }
        });

        modelMapper.addMappings(new PropertyMap<CreateOrderDto, Order>() {
            @Override
            protected void configure() {
                skip().setOrderItems(null);
            }
        });
    }

    @Override
    public OrderDto mapToDto(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }

    @Override
    public Order mapCreateDtoToEntity(CreateOrderDto createOrderDto) {
        return modelMapper.map(createOrderDto, Order.class);
    }
}
