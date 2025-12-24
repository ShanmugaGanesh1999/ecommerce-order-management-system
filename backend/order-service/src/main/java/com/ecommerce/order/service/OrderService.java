package com.ecommerce.order.service;

import com.ecommerce.order.client.ProductServiceClient;
import com.ecommerce.order.dto.*;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.exception.InvalidOperationException;
import com.ecommerce.order.exception.ResourceNotFoundException;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for Order management.
 * Handles business logic for order operations including creation, retrieval, and status updates.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    // Define valid status transitions
    private static final Set<OrderStatus> CANCELLABLE_STATUSES = EnumSet.of(OrderStatus.PENDING, OrderStatus.CONFIRMED);

    /**
     * Create a new order.
     * Validates products, calculates totals, and creates the order.
     *
     * @param request the create order request
     * @return created order DTO
     */
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        log.info("Creating new order for customer: {}", request.getCustomerId());

        // Validate all products and build order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .notes(request.getNotes())
                .build();

        for (OrderItemRequest itemRequest : request.getItems()) {
            // Validate product availability
            productServiceClient.validateProductAvailability(
                    itemRequest.getProductId(),
                    itemRequest.getQuantity()
            );

            // Get product details
            ProductDTO product = productServiceClient.getProduct(itemRequest.getProductId());

            // Calculate subtotal
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            // Create order item
            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice())
                    .subtotal(subtotal)
                    .build();

            order.addItem(orderItem);
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with id: {}", savedOrder.getId());
        return convertToDTO(savedOrder);
    }

    /**
     * Get an order by ID.
     *
     * @param id the order ID
     * @return order DTO
     */
    public OrderDTO getOrderById(Long id) {
        log.debug("Getting order by id: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return convertToDTO(order);
    }

    /**
     * Get all orders for a specific customer with pagination.
     *
     * @param customerId the customer ID
     * @param page page number
     * @param size page size
     * @param sortBy field to sort by
     * @param sortDir sort direction
     * @return page of order DTOs
     */
    public Page<OrderDTO> getOrdersByCustomer(Long customerId, int page, int size,
                                              String sortBy, String sortDir) {
        log.debug("Getting orders for customer: {}, page: {}, size: {}", customerId, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orders = orderRepository.findByCustomerId(customerId, pageable);
        return orders.map(this::convertToDTO);
    }

    /**
     * Get all orders with optional filters and pagination.
     * Admin only operation.
     *
     * @param status filter by status
     * @param page page number
     * @param size page size
     * @param sortBy field to sort by
     * @param sortDir sort direction
     * @return page of order DTOs
     */
    public Page<OrderDTO> getAllOrders(OrderStatus status, int page, int size,
                                       String sortBy, String sortDir) {
        log.debug("Getting all orders - status: {}, page: {}, size: {}", status, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orders;
        if (status != null) {
            orders = orderRepository.findByStatus(status, pageable);
        } else {
            orders = orderRepository.findAll(pageable);
        }

        return orders.map(this::convertToDTO);
    }

    /**
     * Update order status.
     * Admin only operation with validation of status transitions.
     *
     * @param id the order ID
     * @param request the update status request
     * @return updated order DTO
     */
    @Transactional
    public OrderDTO updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        log.info("Updating order status - orderId: {}, newStatus: {}", id, request.getStatus());

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        // Validate status transition
        validateStatusTransition(currentStatus, newStatus);

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        log.info("Order status updated successfully - orderId: {}, status: {}", id, newStatus);
        return convertToDTO(updatedOrder);
    }

    /**
     * Validate order status transition.
     * Ensures only valid state transitions are allowed.
     *
     * @param currentStatus the current status
     * @param newStatus the new status
     * @throws InvalidOperationException if transition is invalid
     */
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Can't change status if already delivered or cancelled
        if (currentStatus == OrderStatus.DELIVERED) {
            throw new InvalidOperationException("Cannot change status of a delivered order");
        }
        if (currentStatus == OrderStatus.CANCELLED) {
            throw new InvalidOperationException("Cannot change status of a cancelled order");
        }

        // Validate specific transitions
        switch (newStatus) {
            case CONFIRMED:
                if (currentStatus != OrderStatus.PENDING) {
                    throw new InvalidOperationException("Can only confirm orders in PENDING status");
                }
                break;
            case SHIPPED:
                if (currentStatus != OrderStatus.CONFIRMED) {
                    throw new InvalidOperationException("Can only ship orders in CONFIRMED status");
                }
                break;
            case DELIVERED:
                if (currentStatus != OrderStatus.SHIPPED) {
                    throw new InvalidOperationException("Can only deliver orders in SHIPPED status");
                }
                break;
            case CANCELLED:
                if (!CANCELLABLE_STATUSES.contains(currentStatus)) {
                    throw new InvalidOperationException("Can only cancel orders in PENDING or CONFIRMED status");
                }
                break;
        }
    }

    /**
     * Convert Order entity to OrderDTO.
     *
     * @param order the order entity
     * @return order DTO
     */
    private OrderDTO convertToDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());

        return OrderDTO.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemDTOs)
                .build();
    }

    /**
     * Convert OrderItem entity to OrderItemDTO.
     *
     * @param item the order item entity
     * @return order item DTO
     */
    private OrderItemDTO convertItemToDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}
