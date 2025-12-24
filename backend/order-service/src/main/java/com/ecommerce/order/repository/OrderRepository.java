package com.ecommerce.order.repository;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Order entity.
 * Provides CRUD operations and custom queries.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find all orders for a specific customer.
     *
     * @param customerId the customer ID
     * @param pageable pagination information
     * @return page of orders
     */
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * Find all orders with a specific status.
     *
     * @param status the order status
     * @param pageable pagination information
     * @return page of orders
     */
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    /**
     * Find orders by date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return page of orders
     */
    Page<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find all orders for a customer with a specific status.
     *
     * @param customerId the customer ID
     * @param status the order status
     * @return list of orders
     */
    List<Order> findByCustomerIdAndStatus(Long customerId, OrderStatus status);
}
