package com.example.pharmacyservice.dto;

public class BillRequest {
    private Double totalAmount;
    private Double amount;
    private Double subTotal;

    public BillRequest() {
    }

    public BillRequest(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getTotalMedicineAmount() {
        if (totalAmount != null) {
            return totalAmount;
        }
        if (amount != null) {
            return amount;
        }
        if (subTotal != null) {
            return subTotal;
        }
        return 0.0;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }
}

