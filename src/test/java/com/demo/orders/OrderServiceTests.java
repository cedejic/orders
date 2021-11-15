package com.demo.orders;

import com.demo.orders.dto.CreateOrderDto;
import com.demo.orders.dto.CreateOrderItemDto;
import com.demo.orders.dto.OrderDto;
import com.demo.orders.model.Order;
import com.demo.orders.model.OrderItem;
import com.demo.orders.model.Product;
import com.demo.orders.repository.OrderRepository;
import com.demo.orders.repository.ProductRepository;
import com.demo.orders.service.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.demo.orders.util.ConversionUtils.toBigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
class OrderServiceTests {

	@Autowired
	private OrderService orderService;

	@MockBean
	private OrderRepository orderRepository;

	@MockBean
	private ProductRepository productRepository;

	private Answer<Order> orderRepositorySaveAnswer;

	@BeforeEach
	public void setup() {
		orderRepositorySaveAnswer = invocation -> {
			Object[] arguments = invocation.getArguments();
			if (arguments != null && arguments.length > 0 && arguments[0] != null && arguments[0] instanceof Order) {
				Order order = (Order)arguments[0];
				if (order.getOrderItems().size() == 0) {
					order.setTotal(BigDecimal.ZERO);
				} else {
					BigDecimal total = order.getOrderItems().stream()
						.map(orderItem -> orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
						.reduce(BigDecimal.ZERO, BigDecimal::add);
					order.setTotal(total);
				}
				return order;
			}
			return null;
		};
	}

	@Test
	@DisplayName("Test place Order SUCCESS")
	void testPlaceOrderSuccess() {
		when(orderRepository.save(any(Order.class))).then(orderRepositorySaveAnswer);

		Product product1 = new Product(1L, "Coffee", toBigDecimal(5.20));
		doReturn(Optional.of(product1)).when(productRepository).findById(product1.getId());

		Product product2 = new Product(2L, "Juice", toBigDecimal(3.75));
		doReturn(Optional.of(product2)).when(productRepository).findById(product2.getId());

		CreateOrderItemDto createOrderItemDto1 = new CreateOrderItemDto(1L, 1);
		CreateOrderItemDto createOrderItemDto2 = new CreateOrderItemDto(2L, 2);
		CreateOrderDto createOrderDto = new CreateOrderDto(
			"new.user@example.com",
			List.of(createOrderItemDto1, createOrderItemDto2));

		OrderDto orderDto = orderService.placeOrder(createOrderDto);

		assertNotNull(orderDto);
		assertEquals("new.user@example.com", orderDto.getCustomerEmail());
		assertEquals(2, orderDto.getItems().size());
		assertEquals(1, orderDto.getItems().get(0).getQuantity());
		assertEquals("Coffee", orderDto.getItems().get(0).getName());
		assertEquals(toBigDecimal(3.75),
			orderDto.getItems().get(1).getPrice().setScale(2, RoundingMode.HALF_EVEN));
		assertEquals(toBigDecimal(12.70),
			orderDto.getTotal().setScale(2, RoundingMode.HALF_EVEN));
	}

	@Test
	@DisplayName("Test place Order with non-existing Product FAIL")
	void testPlaceOrderNonExistingProductFail() {
		Product product1 = new Product(1L, "Coffee", toBigDecimal(5.20));
		doReturn(Optional.of(product1)).when(productRepository).findById(product1.getId());

		CreateOrderItemDto createOrderItemDto1 = new CreateOrderItemDto(5L, 1);
		CreateOrderDto createOrderDto = new CreateOrderDto(
			"new.user@example.com",
			List.of(createOrderItemDto1));

		NoSuchElementException exception = assertThrows(NoSuchElementException.class,
			() -> orderService.placeOrder(createOrderDto));

		assertEquals("Product with id 5 does not exist", exception.getMessage());
	}

	@Test
	@DisplayName("Test get Orders in period SUCCESS")
	void testGetOrdersInPeriodSuccess() {
		LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochMilli(1483228800000L), ZoneId.systemDefault());
		LocalDateTime to = LocalDateTime.ofInstant(Instant.ofEpochMilli(1496275200000L), ZoneId.systemDefault());

		Product product1 = new Product(1L, "Coffee", toBigDecimal(5.20));
		Product product2 = new Product(2L, "Juice", toBigDecimal(3.75));

		LocalDateTime orderPlacedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(1486628800000L), ZoneId.systemDefault());
		Order order1 = new Order(1L, "new.user@example.com", orderPlacedAt, null, null);
		OrderItem orderItem1 = new OrderItem(1L, null, product1, 1, toBigDecimal(5.20));
		OrderItem orderItem2 = new OrderItem(2L, null, product2, 2, toBigDecimal(3.75));
		order1.setOrderItems(List.of(orderItem1, orderItem2));

		doReturn(List.of(order1)).when(orderRepository).getAllOrdersInPeriod(from, to);

		List<OrderDto> orders = orderService.getOrdersInPeriod(1483228800000L, 1496275200000L);

		assertNotNull(orders);
		assertEquals(1, orders.size());
		assertEquals("new.user@example.com", orders.get(0).getCustomerEmail());
		assertEquals(2, orders.get(0).getItems().size());
		assertEquals(1, orders.get(0).getItems().get(0).getQuantity());
		assertEquals("Coffee", orders.get(0).getItems().get(0).getName());
		assertEquals(toBigDecimal(3.75),
			orders.get(0).getItems().get(1).getPrice().setScale(2, RoundingMode.HALF_EVEN));
	}
}
