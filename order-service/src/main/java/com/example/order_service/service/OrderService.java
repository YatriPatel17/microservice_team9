package com.example.order_service.service;

import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.dto.ProductResponse;
import com.example.order_service.model.Order;
import com.example.order_service.repository.OrderRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // Calling product service
    @Autowired
    private ProductClient productClient;

    // Getting all orders using list
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // finds order by id
    public Order getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        // if order null it returns null
        return order.orElse(null);
    }

    // Creating new order
    public Order createOrder(OrderRequest orderRequest) {
        try {
            System.out.println("Creating order for productId: " + orderRequest.getProductId());

            // Get product detail from product service
            ProductResponse product = productClient.getProductById(orderRequest.getProductId());

            System.out.println("Product response: " + product);

            // checking product is available or not
            if (product == null) {
                throw new RuntimeException("Product not found with id: " + orderRequest.getProductId());
            }

            System.out.println("Product found: " + product.getName() +
                    ", Price: " + product.getPrice() +
                    ", Quantity: " + product.getQuantity());

            // Checking product quantity is available
            if (product.getQuantity() < orderRequest.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock. Available: " + product.getQuantity() +
                                ", Requested: " + orderRequest.getQuantity()
                );
            }

            // Calculating total price
            double totalPrice = product.getPrice() * orderRequest.getQuantity();

            // set default status
            String status = orderRequest.getStatus();
            if (status == null || status.trim().isEmpty()) {
                status = "Pending";
            }

            // Create and save order
            Order order = new Order(
                    orderRequest.getProductId(),
                    orderRequest.getQuantity(),
                    totalPrice,
                    status
            );

            Order savedOrder = orderRepository.save(order);
            System.out.println("Order saved successfully: " + savedOrder.getId());

            return savedOrder;

        } catch (FeignException e) {
            System.err.println("FeignException: " + e.getMessage());
            System.err.println("Status: " + e.status());
            System.err.println("Content: " + e.contentUTF8());
            throw new RuntimeException("Error calling product service: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Exception in createOrder: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error creating order: " + e.getMessage());
        }
    }
}