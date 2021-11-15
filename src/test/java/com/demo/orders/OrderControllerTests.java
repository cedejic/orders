package com.demo.orders;

import com.demo.orders.dto.CreateOrderDto;
import com.demo.orders.dto.CreateOrderItemDto;
import com.demo.orders.dto.OrderDto;
import com.demo.orders.dto.OrderItemDto;
import com.demo.orders.service.order.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.demo.orders.util.ConversionUtils.toBigDecimal;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    @DisplayName("Test get Order SUCCESS")
    void testGetOrderSuccess() throws Exception {
        OrderDto firstOrder = prepareFirstOrder();

        when(orderService.getOrder(1L)).thenReturn(firstOrder);

        mockMvc.perform(get("/orders/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerEmail", equalTo("john.doe@example.com")))
            .andExpect(jsonPath("$.placedAt", equalTo("2015-10-20 12:00:00")))
            .andExpect(jsonPath("$.total", equalTo(13.1d))) // issue with jsonPath casting value to Double
            .andExpect(jsonPath("$.items", hasSize(2)));

        verify(orderService).getOrder(any());
    }

    @Test
    @DisplayName("Test get non-existant Order FAIL")
    void testGetNonExistantOrderFail() throws Exception {
        when(orderService.getOrder(anyLong())).thenReturn(null);

        mockMvc.perform(get("/orders/15"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());

        verify(orderService).getOrder(any());
    }

    @Test
    @DisplayName("Test invalid argument sent to get Order FAIL")
    void testInvalidArgumentSentToGetOrderFail() throws Exception {
        mockMvc.perform(get("/orders/aaa"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("Test get Order without id FAIL")
    void testGetOrderWithoutIdFail() throws Exception {
        mockMvc.perform(get("/orders/"))
            .andDo(print())
            .andExpect(status().isMethodNotAllowed())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("Test get all Orders SUCCESS")
    void testGetAllOrdersSuccess() throws Exception {
        OrderDto firstOrder = prepareFirstOrder();
        OrderDto secondOrder = prepareSecondOrder();

        when(orderService.getAllOrders()).thenReturn(List.of(firstOrder, secondOrder));

        mockMvc.perform(get("/orders/all"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$.[?(@.id == 1 && @.customerEmail == \"john.doe@example.com\" && @.placedAt == \"2015-10-20 12:00:00\")]").exists())
            .andExpect(jsonPath("$.[?(@.id == 2 && @.customerEmail == \"nell.goodwin@example.com\" && @.placedAt == \"2017-01-01 17:25:13\")]").exists());

        verify(orderService).getAllOrders();
    }

    @Test
    @DisplayName("Test place Order SUCCESS")
    void testPlaceOrderSuccess() throws Exception {
        CreateOrderDto createOrderRequest = prepareCreateOrder();
        OrderDto firstOrder = prepareCreateOrderResponse();

        when(orderService.placeOrder(any(CreateOrderDto.class))).thenReturn(firstOrder);

        mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createOrderRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerEmail", equalTo("best.client@example.com")))
            .andExpect(jsonPath("$.placedAt", equalTo("2021-11-15 13:45:11")))
            .andExpect(jsonPath("$.total", equalTo(14.15d))); // issue with jsonPath casting value to Double

        verify(orderService).placeOrder(any());
    }

    @Test
    @DisplayName("Test place Order with invalid request FAIL")
    void testPlaceOrderWithInvalidRequestFail() throws Exception {
        CreateOrderDto invalidCreateOrderRequest = prepareInvalidCreateOrder();

        mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidCreateOrderRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", equalTo("Validation failed")))
            .andExpect(jsonPath("$.details", hasSize(2)))
            .andExpect(jsonPath("$.details", containsInAnyOrder("must not be empty", "must not be null")));
    }

    @Test
    @DisplayName("Test get Orders in time period SUCCESS")
    void testGetOrdersInTimePeriodSuccess() throws Exception {
        OrderDto firstOrder = prepareFirstOrder();
        OrderDto secondOrder = prepareSecondOrder();

        when(orderService.getOrdersInPeriod(1483228800000L, 1496275200000L)).thenReturn(List.of(firstOrder, secondOrder));

        mockMvc.perform(get("/orders/in-period")
            .queryParam("from", "1483228800000")
            .queryParam("to", "1496275200000"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$.[?(@.id == 1 && @.customerEmail == \"john.doe@example.com\" && @.placedAt == \"2015-10-20 12:00:00\")]").exists())
            .andExpect(jsonPath("$.[?(@.id == 2 && @.customerEmail == \"nell.goodwin@example.com\" && @.placedAt == \"2017-01-01 17:25:13\")]").exists());

        verify(orderService).getOrdersInPeriod(any(), any());
    }

    @Test
    @DisplayName("Test get Orders in time period with invalid query param FAIL")
    void testGetOrdersInTimePeriodWithInvalidQueryParamFail() throws Exception {
        mockMvc.perform(get("/orders/in-period")
            .queryParam("from", "1483228800000")
            .queryParam("to", ""))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$").doesNotExist());
    }

    private OrderDto prepareFirstOrder() {
        OrderItemDto orderItemDto1 = new OrderItemDto("Coffee", 1, toBigDecimal(5.20));
        OrderItemDto orderItemDto2 = new OrderItemDto("Juice", 1, toBigDecimal(3.75));

        return new OrderDto(1L,
            "john.doe@example.com",
            LocalDateTime.parse("2015-10-20 12:00:00", dateTimeFormatter),
            toBigDecimal(13.10),
            List.of(orderItemDto1, orderItemDto2));
    }

    private OrderDto prepareSecondOrder() {
        OrderItemDto orderItemDto1 = new OrderItemDto("Chocolate cake", 2, toBigDecimal(6.70));
        OrderItemDto orderItemDto2 = new OrderItemDto("Juice", 2, toBigDecimal(3.75));

        return new OrderDto(2L,
            "nell.goodwin@example.com",
            LocalDateTime.parse("2017-01-01 17:25:13", dateTimeFormatter),
            toBigDecimal(17.15),
            List.of(orderItemDto1, orderItemDto2));
    }

    private CreateOrderDto prepareCreateOrder() {
        CreateOrderItemDto item1 = new CreateOrderItemDto(1L, 2);
        CreateOrderItemDto item2 = new CreateOrderItemDto(2L, 1);

        return new CreateOrderDto("best.client@example.com", List.of(item1, item2));
    }

    private OrderDto prepareCreateOrderResponse() {
        OrderItemDto orderItemDto1 = new OrderItemDto("Coffee", 2, toBigDecimal(5.20));
        OrderItemDto orderItemDto2 = new OrderItemDto("Juice", 1, toBigDecimal(3.75));

        return new OrderDto(3L,
            "best.client@example.com",
            LocalDateTime.parse("2021-11-15 13:45:11", dateTimeFormatter),
            toBigDecimal(14.15),
            List.of(orderItemDto1, orderItemDto2));
    }

    private CreateOrderDto prepareInvalidCreateOrder() {
        CreateOrderItemDto item1 = new CreateOrderItemDto(null, 2);
        CreateOrderItemDto item2 = new CreateOrderItemDto(2L, 1);

        return new CreateOrderDto(null, List.of(item1, item2));
    }
}
