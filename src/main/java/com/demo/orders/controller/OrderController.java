package com.demo.orders.controller;

import com.demo.orders.dto.CreateOrderDto;
import com.demo.orders.dto.OrderDto;
import com.demo.orders.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public OrderDto getOrder(@NotNull @PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @GetMapping("/all")
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping
    public OrderDto placeOrder(@Valid @NotNull @RequestBody CreateOrderDto createOrderDto) {
        return orderService.placeOrder(createOrderDto);
    }

    @GetMapping("/in-period")
    public List<OrderDto> getOrdersInTimePeriod(@NotNull @RequestParam("from") Long timeFrom, @NotNull @RequestParam("to") Long timeTo) {
        return orderService.getOrdersInPeriod(timeFrom, timeTo);
    }
}
