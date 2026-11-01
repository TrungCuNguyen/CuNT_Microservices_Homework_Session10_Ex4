package com.example.pharmacyservice.controller;

import com.example.pharmacyservice.dto.BillRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bill")
@RefreshScope
public class BillController {

    @Value("${pharmacy.vat-rate:0.08}")
    private double vatRate;

    @PostMapping
    public ResponseEntity<Map<String, Object>> calculateBill(
            @RequestBody(required = false) BillRequest request,
            @RequestParam(name = "totalAmount", required = false) Double paramTotalAmount) {

        double medicineAmount = 0.0;
        if (request != null && request.getTotalMedicineAmount() > 0) {
            medicineAmount = request.getTotalMedicineAmount();
        } else if (paramTotalAmount != null && paramTotalAmount > 0) {
            medicineAmount = paramTotalAmount;
        } else {
            // Giá trị mặc định minh họa nếu không truyền
            medicineAmount = 100000.0;
        }

        // Tính toán tỷ lệ phần trăm VAT
        double effectiveRate = (vatRate > 1.0) ? (vatRate / 100.0) : vatRate;
        double vatAmount = medicineAmount * effectiveRate;
        double finalTotal = medicineAmount + vatAmount;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("medicineTotalAmount", medicineAmount);
        response.put("vatRateConfigured", vatRate);
        response.put("vatRatePercent", String.format("%.1f%%", effectiveRate * 100));
        response.put("vatAmount", vatAmount);
        response.put("finalTotalAmount", finalTotal);
        response.put("message", "Tính hóa đơn thành công (Tổng tiền thuốc + VAT)");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/current-vat")
    public ResponseEntity<Map<String, Object>> getCurrentVat() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("vatRateConfigured", vatRate);
        response.put("vatRatePercent", String.format("%.1f%%", ((vatRate > 1.0) ? (vatRate / 100.0) : vatRate) * 100));
        return ResponseEntity.ok(response);
    }
}

