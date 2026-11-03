package com.example.inventoryservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medicine_id")
    private Long medicineId;

    @Column(name = "price_sell")
    private Double priceSell;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "timestamp")
    private Long timestamp;

    @Column(name = "total_amount")
    private Double totalAmount;

    public Order() {
    }

    public Order(Long medicineId, Double priceSell, Integer quantity, Long timestamp, Double totalAmount) {
        this.medicineId = medicineId;
        this.priceSell = priceSell;
        this.quantity = quantity;
        this.timestamp = timestamp;
        this.totalAmount = totalAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    public Double getPriceSell() {
        return priceSell;
    }

    public void setPriceSell(Double priceSell) {
        this.priceSell = priceSell;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", medicineId=" + medicineId +
                ", priceSell=" + priceSell +
                ", quantity=" + quantity +
                ", timestamp=" + timestamp +
                ", totalAmount=" + totalAmount +
                '}';
    }
}

