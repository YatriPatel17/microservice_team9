package com.example.order_service.dto;

public class OrderRequest {

    private Long productId;
    private Integer quantity;
    private String status; // Optional: if you want to allow status in request

    // Getter and setter
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}