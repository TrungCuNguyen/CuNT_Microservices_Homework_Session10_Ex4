package com.example.pharmacyservice.dto;

public class CheckoutRequest {

    private String orderId;
    private String medicineId;
    private Integer quantity;
    private Double unitPrice;

    public CheckoutRequest() {
    }

    public CheckoutRequest(String orderId, String medicineId, Integer quantity, Double unitPrice) {
        this.orderId = orderId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
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
        return quantity != null ? quantity : 1;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice != null ? unitPrice : 50000.0;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }
}

