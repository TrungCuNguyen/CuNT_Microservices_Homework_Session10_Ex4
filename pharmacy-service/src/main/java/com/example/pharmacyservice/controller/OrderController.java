package com.example.pharmacyservice.controller;

import com.example.pharmacyservice.dto.CheckoutRequest;
import com.example.pharmacyservice.event.OrderEvent;
import com.example.pharmacyservice.service.OrderProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pharmacy")
public class OrderController {

    @Autowired
    private OrderProducerService orderProducerService;

    @Value("${pharmacy.vat-rate:0.10}")
    private double vatRate;

    @Value("${app.kafka.topics.medicine-stock-events:medicine-stock-events}")
    private String topicName;

    /**
     * API bán thuốc / Thanh toán đơn hàng
     * Khi nhân viên bấm "Thanh toán", hệ thống gửi sự kiện OrderEvent vào topic medicine-stock-events
     * Message Key là medicineId để đảm bảo cùng loại thuốc luôn đi vào cùng 1 Partition
     */
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody(required = false) CheckoutRequest request) {
        if (request == null) {
            request = new CheckoutRequest();
        }

        String orderId = request.getOrderId();
        if (orderId == null || orderId.trim().isEmpty()) {
            orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        String medicineId = request.getMedicineId();
        if (medicineId == null || medicineId.trim().isEmpty()) {
            medicineId = "MED_PARACETAMOL";
        }

        int quantity = request.getQuantity();
        double unitPrice = request.getUnitPrice();
        double subTotal = unitPrice * quantity;
        double effectiveRate = (vatRate > 1.0) ? (vatRate / 100.0) : vatRate;
        double vatAmount = subTotal * effectiveRate;
        double finalTotal = subTotal + vatAmount;

        // 1. Tạo đối tượng OrderEvent theo đúng yêu cầu đề bài
        OrderEvent orderEvent = new OrderEvent(
                orderId,
                medicineId,
                quantity,
                System.currentTimeMillis()
        );

        // 2. Gửi OrderEvent vào Kafka với Message Key là medicineId
        orderProducerService.sendOrderEvent(orderEvent);

        // 3. Trả về kết quả hóa đơn và thông tin sự kiện
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "SUCCESS");
        response.put("action", "CHECKOUT_COMPLETED");
        response.put("message", "Thanh toán đơn hàng thành công và đã phát sự kiện OrderEvent lên Kafka");
        response.put("orderId", orderId);
        response.put("medicineId", medicineId);
        response.put("quantity", quantity);
        response.put("subTotal", subTotal);
        response.put("vatAmount", vatAmount);
        response.put("finalTotal", finalTotal);

        Map<String, Object> kafkaInfo = new LinkedHashMap<>();
        kafkaInfo.put("topic", topicName);
        kafkaInfo.put("messageKey", medicineId);
        kafkaInfo.put("partitioningStrategy", "Cùng medicineId được phân phối vào cùng 1 Partition");
        kafkaInfo.put("payload", orderEvent);

        response.put("kafkaEvent", kafkaInfo);

        return ResponseEntity.ok(response);
    }
}

