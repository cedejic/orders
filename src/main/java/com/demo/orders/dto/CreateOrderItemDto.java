package com.demo.orders.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderItemDto {

    @NotNull
    private Long productId;

    @NotNull
    @Min(value = 1)
    private Integer quantity;
}
