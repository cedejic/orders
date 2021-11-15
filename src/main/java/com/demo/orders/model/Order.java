package com.demo.orders.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "purchase_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column
    private String customerEmail;

    @NotNull
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime placedAt;

    @OneToMany(
        cascade = CascadeType.ALL,
        mappedBy = "order",
        fetch = FetchType.LAZY,
        orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Transient
    private BigDecimal total;

    @PostLoad
    @PostPersist
    @PostUpdate
    private void postLoad() {
        this.total = this.orderItems.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
