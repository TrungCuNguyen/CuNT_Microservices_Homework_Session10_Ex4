package com.example.pharmacyservice;

import com.example.pharmacyservice.event.OrderEvent;
import com.example.pharmacyservice.service.OrderProducerService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(partitions = 3, topics = {"medicine-stock-events"}, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@TestPropertySource(properties = {
        "spring.config.import=",
        "spring.datasource.url=jdbc:postgresql://localhost:5432/pharmacy_db",
        "spring.datasource.username=postgres",
        "spring.datasource.password=17111998",
        "spring.datasource.driver-class-name=org.postgresql.Driver",
        "spring.jpa.hibernate.ddl-auto=none"
})
public class OrderKafkaProducerTest {

    @Autowired
    private OrderProducerService orderProducerService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    public void testSameMedicineIdRoutesToSamePartition() throws Exception {
        String medicineId = "MED_PARACETAMOL_500";

        // Đơn hàng 1
        OrderEvent order1 = new OrderEvent("ORD-001", medicineId, 3);
        CompletableFuture<SendResult<String, OrderEvent>> future1 = orderProducerService.sendOrderEvent(order1);
        SendResult<String, OrderEvent> result1 = future1.get();

        // Đơn hàng 2 (cùng loại thuốc medicineId)
        OrderEvent order2 = new OrderEvent("ORD-002", medicineId, 10);
        CompletableFuture<SendResult<String, OrderEvent>> future2 = orderProducerService.sendOrderEvent(order2);
        SendResult<String, OrderEvent> result2 = future2.get();

        // Kiểm tra Message Key
        assertEquals(medicineId, result1.getProducerRecord().key());
        assertEquals(medicineId, result2.getProducerRecord().key());

        // THỬ THÁCH ĐỀ BÀI: Cùng một loại thuốc (cùng key medicineId) LUÔN đi vào CÙNG MỘT Partition!
        int partition1 = result1.getRecordMetadata().partition();
        int partition2 = result2.getRecordMetadata().partition();

        System.out.println("==================================================================");
        System.out.println(">>> [KAFKA PARTITION TEST]");
        System.out.println(">>> Đơn hàng 1 (Key: " + medicineId + ") -> Partition: " + partition1);
        System.out.println(">>> Đơn hàng 2 (Key: " + medicineId + ") -> Partition: " + partition2);
        System.out.println("==================================================================");

        assertEquals(partition1, partition2, "Hai đơn hàng có cùng medicineId phải được định tuyến vào cùng một Partition!");
    }
}
