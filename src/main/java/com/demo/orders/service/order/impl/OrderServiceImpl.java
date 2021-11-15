package com.demo.orders.service.order.impl;

import com.demo.orders.dto.CreateOrderDto;
import com.demo.orders.dto.OrderDto;
import com.demo.orders.model.Order;
import com.demo.orders.model.OrderItem;
import com.demo.orders.model.Product;
import com.demo.orders.repository.OrderRepository;
import com.demo.orders.repository.ProductRepository;
import com.demo.orders.service.order.OrderMapper;
import com.demo.orders.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderDto placeOrder(CreateOrderDto createOrderDto) {
        Order order = orderMapper.mapCreateDtoToEntity(createOrderDto);

        order.setPlacedAt(LocalDateTime.now());
        List<OrderItem> items = createOrderDto.getOrderItems().stream()
            .map(orderProductDto -> {
                Product product = productRepository.findById(orderProductDto.getProductId())
                    .orElseThrow(() -> new NoSuchElementException("Product with id " + orderProductDto.getProductId() + " does not exist"));

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(orderProductDto.getQuantity());
                orderItem.setPrice(product.getPrice());
                return orderItem;
            })
            .collect(Collectors.toList());
        order.setOrderItems(items);

        return orderMapper.mapToDto(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersInPeriod(Long timeFrom, Long timeTo) {
        LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeFrom), ZoneId.systemDefault());
        LocalDateTime to = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeTo), ZoneId.systemDefault());

        return orderRepository.getAllOrdersInPeriod(from, to)
            .stream()
            .map(orderMapper::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrder(Long id) {
        return orderRepository.findOrderById(id)
            .map(orderMapper::mapToDto)
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        return orderRepository.getAllOrders()
            .stream()
            .map(orderMapper::mapToDto)
            .collect(Collectors.toList());
    }
}
