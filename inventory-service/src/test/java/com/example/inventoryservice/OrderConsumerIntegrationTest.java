package com.example.inventoryservice;

import com.example.inventoryservice.entity.Medicine;
import com.example.inventoryservice.entity.Order;
import com.example.inventoryservice.event.OrderEvent;
import com.example.inventoryservice.repository.MedicineRepository;
import com.example.inventoryservice.repository.OrderRepository;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(partitions = 3, topics = {"medicine-stock-events"}, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/pharmacy_db",
        "spring.datasource.username=postgres",
        "spring.datasource.password=17111998",
        "spring.datasource.driver-class-name=org.postgresql.Driver",
        "spring.jpa.hibernate.ddl-auto=update"
})
public class OrderConsumerIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void setup() {
        Medicine med = new Medicine(1L, "Panadol Extra", "Thuốc giảm đau hạ sốt", 50000.0, 100);
        medicineRepository.save(med);
    }

    @Test
    public void testConsumerDeductsStockAndCreatesOrder() throws Exception {
        Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        KafkaTemplate<String, OrderEvent> testKafkaTemplate =
                new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));

        // Bán 5 sản phẩm thuốc id=1
        OrderEvent event = new OrderEvent("ORD-TEST-999", "1", 5, 50000.0, 250000.0, System.currentTimeMillis());
        testKafkaTemplate.send("medicine-stock-events", "1", event).get();

        // Chờ consumer xử lý xong trong vòng tối đa 10 giây
        long startTime = System.currentTimeMillis();
        boolean processed = false;
        while (System.currentTimeMillis() - startTime < 10000) {
            Medicine updatedMed = medicineRepository.findById(1L).orElse(null);
            if (updatedMed != null && updatedMed.getQuantity() == 95) {
                processed = true;
                break;
            }
            Thread.sleep(300);
        }

        assertTrue(processed, "Số lượng tồn kho phải được trừ từ 100 xuống 95!");

        Medicine resultMed = medicineRepository.findById(1L).orElseThrow();
        assertEquals(95, resultMed.getQuantity());

        List<Order> orders = orderRepository.findAll();
        assertFalse(orders.isEmpty());
        Order latestOrder = orders.get(orders.size() - 1);
        assertEquals(1L, latestOrder.getMedicineId());
        assertEquals(5, latestOrder.getQuantity());
        assertEquals(250000.0, latestOrder.getTotalAmount());
    }
}

