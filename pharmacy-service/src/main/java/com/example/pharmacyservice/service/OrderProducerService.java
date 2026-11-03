package com.example.pharmacyservice.service;

import com.example.pharmacyservice.event.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderProducerService {

    private static final Logger log = LoggerFactory.getLogger(OrderProducerService.class);

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Value("${app.kafka.topics.medicine-stock-events:medicine-stock-events}")
    private String topicName;

    /**
     * Gửi OrderEvent vào topic medicine-stock-events với Message Key là medicineId.
     * Sử dụng medicineId làm Key giúp Kafka phân chia (partitioning) tất cả các đơn hàng
     * của cùng một loại thuốc vào cùng một Partition duy nhất.
     */
    public CompletableFuture<SendResult<String, OrderEvent>> sendOrderEvent(OrderEvent orderEvent) {
        String messageKey = orderEvent.getMedicineId();

        log.info(">>> [KAFKA PRODUCER] Chuẩn bị gửi OrderEvent vào topic '{}' với Message Key='{}': {}", 
                topicName, messageKey, orderEvent);

        CompletableFuture<SendResult<String, OrderEvent>> future = kafkaTemplate.send(topicName, messageKey, orderEvent);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(">>> [KAFKA GỬI THÀNH CÔNG] Order ID: {}, Topic: {}, Partition: {}, Offset: {}, Key: {}",
                        orderEvent.getOrderId(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        messageKey);
            } else {
                log.warn(">>> [KAFKA LƯU Ý] Không kết nối được Kafka broker ({}): {}", 
                        ex.getClass().getSimpleName(), ex.getMessage());
            }
        });

        return future;
    }
}

