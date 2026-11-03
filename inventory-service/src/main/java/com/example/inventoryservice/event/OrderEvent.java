package com.example.inventoryservice.event;

import java.io.Serializable;

public class OrderEvent implements Serializable {

    private String orderId;
    private String medicineId;
    private Integer quantity;
    private Double priceSell;
    private Double totalAmount;
    private Long timestamp;

    public OrderEvent() {
    }

    public OrderEvent(String orderId, String medicineId, Integer quantity, Double priceSell, Double totalAmount, Long timestamp) {
        this.orderId = orderId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.priceSell = priceSell;
        this.totalAmount = totalAmount;
        this.timestamp = timestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPriceSell() {
        return priceSell;
    }

    public void setPriceSell(Double priceSell) {
        this.priceSell = priceSell;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "OrderEvent{" +
                "orderId='" + orderId + '\'' +
                ", medicineId='" + medicineId + '\'' +
                ", quantity=" + quantity +
                ", priceSell=" + priceSell +
                ", totalAmount=" + totalAmount +
                ", timestamp=" + timestamp +
                '}';
    }
}

