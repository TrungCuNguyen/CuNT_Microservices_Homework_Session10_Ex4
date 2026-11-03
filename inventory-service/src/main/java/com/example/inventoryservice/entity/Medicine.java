package com.example.inventoryservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "medicinse")
public class Medicine {

    @Id
    private Long id;

    @Column(name = "medicine_name")
    private String medicineName;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Double price;

    @Column(name = "quantity")
    private Integer quantity;

    public Medicine() {
    }

    public Medicine(Long id, String medicineName, String description, Double price, Integer quantity) {
        this.id = id;
        this.medicineName = medicineName;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // Helper getter/setter for stock
    public Integer getStock() {
        return quantity;
    }

    public void setStock(Integer stock) {
        this.quantity = stock;
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + id +
                ", medicineName='" + medicineName + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}

