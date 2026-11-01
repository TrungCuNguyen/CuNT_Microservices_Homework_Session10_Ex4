package com.example.pharmacyservice.service;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class WarehouseService {

    private static final Logger log = LoggerFactory.getLogger(WarehouseService.class);

    @Autowired
    private RestTemplate restTemplate;

    // Giả lập trạng thái sập của warehouse-service (mặc định true để test ngắt mạch)
    private boolean simulatedDown = true;

    public void setSimulatedDown(boolean simulatedDown) {
        this.simulatedDown = simulatedDown;
    }

    public boolean isSimulatedDown() {
        return simulatedDown;
    }

    /**
     * Hàm gọi kho tổng với CircuitBreaker warehouseCB
     */
    @CircuitBreaker(name = "warehouseCB", fallbackMethod = "checkStockFallback")
    public Map<String, Object> checkStock(String medicineId, int quantity) {
        log.info(">>> [CALL WAREHOUSE] Đang gọi sang warehouse-service kiểm tra tồn kho mã: {}, số lượng: {}", medicineId, quantity);

        if (simulatedDown) {
            // Giả lập warehouse-service phản hồi chậm hoặc sập hoàn toàn
            throw new RuntimeException("warehouse-service (Kho tổng) bị sập hoặc mất kết nối!");
        }

        // Khi kho tổng hoạt động bình thường:
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("medicineId", medicineId);
        result.put("quantityRequested", quantity);
        result.put("inStock", true);
        result.put("availableStock", 500);
        result.put("status", "SUCCESS");
        result.put("message", "Kiểm tra kho thành công: Đủ hàng xuất bán lẻ");
        return result;
    }

    /**
     * Fallback method khi gọi warehouse-service bị lỗi hoặc khi mạch đã OPEN
     */
    public Map<String, Object> checkStockFallback(String medicineId, int quantity, Throwable throwable) {
        Map<String, Object> fallbackResult = new LinkedHashMap<>();
        fallbackResult.put("medicineId", medicineId);
        fallbackResult.put("quantityRequested", quantity);
        fallbackResult.put("inStock", false);

        if (throwable instanceof CallNotPermittedException) {
            log.warn(">>> [CIRCUIT BREAKER OPEN - FAIL-FAST] Mạch warehouseCB đang OPEN! Lập tức trả về lỗi nhanh (Fail-fast) không chờ đợi.");
            fallbackResult.put("circuitState", "OPEN");
            fallbackResult.put("behavior", "FAIL-FAST");
            fallbackResult.put("message", "Mạch đã ngắt (OPEN): Dịch vụ kho tổng đang gặp sự cố, hệ thống tự động bảo vệ tránh treo máy tính tiền.");
        } else {
            log.error(">>> [CIRCUIT BREAKER FAILURE] Lỗi khi gọi kho: {}", throwable.getMessage());
            fallbackResult.put("circuitState", "RECORDING_FAILURE");
            fallbackResult.put("behavior", "FALLBACK");
            fallbackResult.put("message", "Lỗi kết nối kho tổng: " + throwable.getMessage());
        }

        return fallbackResult;
    }
}

