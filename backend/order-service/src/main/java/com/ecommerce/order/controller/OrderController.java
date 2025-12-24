package com.ecommerce.order.controller;

import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.OrderDTO;
import com.ecommerce.order.dto.UpdateOrderStatusRequest;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Order management.
 * Provides endpoints for order CRUD operations and status updates.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    /**
     * Create a new order.
     *
     * @param request the create order request
     * @return created order
     */
    @PostMapping
    @Operation(summary = "Create order", description = "Create a new order with items")
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /api/v1/orders - customerId: {}", request.getCustomerId());
        OrderDTO order = orderService.createOrder(request);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    /**
     * Get an order by ID.
     *
     * @param id the order ID
     * @return order details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Get a single order by its ID")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        log.info("GET /api/v1/orders/{}", id);
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Get all orders for a specific customer with pagination.
     *
     * @param customerId the customer ID
     * @param page page number (default 0)
     * @param size page size (default 10)
     * @param sortBy field to sort by (default "orderDate")
     * @param sortDir sort direction (default "desc")
     * @return page of customer orders
     */
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get customer orders", description = "Get all orders for a specific customer with pagination")
    public ResponseEntity<Page<OrderDTO>> getOrdersByCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("GET /api/v1/orders/customer/{} - page: {}, size: {}", customerId, page, size);
        Page<OrderDTO> orders = orderService.getOrdersByCustomer(customerId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get all orders with optional filters and pagination.
     * Admin only operation.
     *
     * @param status filter by status
     * @param page page number (default 0)
     * @param size page size (default 10)
     * @param sortBy field to sort by (default "orderDate")
     * @param sortDir sort direction (default "desc")
     * @return page of all orders
     */
    @GetMapping
    @Operation(summary = "Get all orders", description = "Get all orders with optional filters and pagination (Admin only)")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("GET /api/v1/orders - status: {}, page: {}, size: {}", status, page, size);
        Page<OrderDTO> orders = orderService.getAllOrders(status, page, size, sortBy, sortDir);
        return ResponseEntity.ok(orders);
    }

    /**
     * Update order status.
     * Admin only operation.
     *
     * @param id the order ID
     * @param request the update status request
     * @return updated order
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Update the status of an order (Admin only)")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        log.info("PUT /api/v1/orders/{}/status - newStatus: {}", id, request.getStatus());
        OrderDTO order = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(order);
    }
}
