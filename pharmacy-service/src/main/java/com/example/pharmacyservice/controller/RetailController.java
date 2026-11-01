package com.example.pharmacyservice.controller;

import com.example.pharmacyservice.service.WarehouseService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pharmacy")
public class RetailController {

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    /**
     * Bán lẻ và gọi kiểm tra kho tổng
     */
    @GetMapping("/check-stock")
    public ResponseEntity<Map<String, Object>> checkStock(
            @RequestParam(defaultValue = "MED_PARACETAMOL") String medicineId,
            @RequestParam(defaultValue = "2") int quantity) {

        long startTime = System.currentTimeMillis();
        Map<String, Object> result = warehouseService.checkStock(medicineId, quantity);
        long responseTime = System.currentTimeMillis() - startTime;

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("warehouseCB");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("executionTimeMs", responseTime);
        response.put("currentCircuitBreakerState", cb.getState().name());
        response.put("failureRate", cb.getMetrics().getFailureRate() + "%");
        response.put("numberOfFailedCalls", cb.getMetrics().getNumberOfFailedCalls());
        response.put("numberOfSuccessfulCalls", cb.getMetrics().getNumberOfSuccessfulCalls());
        response.put("numberOfNotPermittedCalls", cb.getMetrics().getNumberOfNotPermittedCalls());
        response.put("result", result);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint để giả lập bật / tắt trạng thái sập của warehouse-service
     */
    @PostMapping("/simulate-warehouse")
    public ResponseEntity<Map<String, Object>> simulateWarehouse(@RequestParam boolean down) {
        warehouseService.setSimulatedDown(down);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("simulatedDown", down);
        response.put("message", down ? "Đã bật giả lập sập kho tổng" : "Đã bật giả lập kho tổng hoạt động bình thường");
        return ResponseEntity.ok(response);
    }

    /**
     * Kiểm tra trạng thái hiện tại của CircuitBreaker warehouseCB
     */
    @GetMapping("/circuit-status")
    public ResponseEntity<Map<String, Object>> getCircuitStatus() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("warehouseCB");
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("circuitName", "warehouseCB");
        response.put("state", cb.getState().name());
        response.put("failureRateThreshold", cb.getCircuitBreakerConfig().getFailureRateThreshold() + "%");
        response.put("waitDurationInOpenState", cb.getCircuitBreakerConfig().getWaitIntervalFunctionInOpenState().apply(1) + "ms");
        response.put("metrics", Map.of(
                "failureRate", cb.getMetrics().getFailureRate(),
                "numberOfBufferedCalls", cb.getMetrics().getNumberOfBufferedCalls(),
                "numberOfFailedCalls", cb.getMetrics().getNumberOfFailedCalls(),
                "numberOfSuccessfulCalls", cb.getMetrics().getNumberOfSuccessfulCalls(),
                "numberOfNotPermittedCalls", cb.getMetrics().getNumberOfNotPermittedCalls()
        ));
        return ResponseEntity.ok(response);
    }
}

