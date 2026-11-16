package com.parquesoft.ecommerce.service;

import com.parquesoft.ecommerce.entity.OrderEntity;
import com.parquesoft.ecommerce.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Debe crear un pedido correctamente")
    void testCreateOrder_Success() {

        OrderEntity order = new OrderEntity();
        order.setCustomerId(1L);
        order.setTotalAmount(150000.0);

        when(orderRepository.save(any(OrderEntity.class))).thenReturn(order);

        OrderEntity result = orderService.createOrder(order);

        assertNotNull(result);
        assertEquals("PENDING", order.getStatus());

        verify(orderRepository, times(1)).save(any(OrderEntity.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el monto es menor o igual a cero")
    void testCreateOrder_ThrowsException_WhenTotalIsZeroOrNegative() {

        OrderEntity order = new OrderEntity();
        order.setCustomerId(1L);
        order.setTotalAmount(-10.0);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(order));

        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    @DisplayName("Debe retornar un pedido por ID")
    void testGetOrderById_Success() {

        OrderEntity order = new OrderEntity();
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Optional<OrderEntity> result = orderService.getOrderById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    @DisplayName("Debe retornar Optional.empty cuando no existe")
    void testGetOrderById_NotFound() {

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<OrderEntity> result = orderService.getOrderById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe cancelar correctamente un pedido")
    void testCancelOrder_Success() {

        OrderEntity order = new OrderEntity();
        order.setStatus("PENDING");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        assertEquals("CANCELLED", order.getStatus());

        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el pedido fue enviado")
    void testCancelOrder_ThrowException_WhenShipped() {

        OrderEntity order = new OrderEntity();
        order.setStatus("SHIPPED");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(1L));
    }
}