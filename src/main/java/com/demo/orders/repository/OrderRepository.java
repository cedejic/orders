package com.demo.orders.repository;

import com.demo.orders.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o join fetch o.orderItems oi join fetch oi.product p where o.id = :id")
    Optional<Order> findOrderById(Long id);

    @Query("select DISTINCT o from Order o join fetch o.orderItems oi join fetch oi.product p")
    List<Order> getAllOrders();

    @Query("select DISTINCT o from Order o join fetch o.orderItems oi join fetch oi.product p where o.placedAt >= :from and o.placedAt <= :to")
    List<Order> getAllOrdersInPeriod(LocalDateTime from, LocalDateTime to);
}
