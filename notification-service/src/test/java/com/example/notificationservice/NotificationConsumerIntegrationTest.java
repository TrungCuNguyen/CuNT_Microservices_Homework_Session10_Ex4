package com.example.notificationservice;

import com.example.notificationservice.entity.Medicine;
import com.example.notificationservice.event.OrderEvent;
import com.example.notificationservice.repository.MedicineRepository;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@EmbeddedKafka(partitions = 3, topics = {"medicine-stock-events"}, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/pharmacy_db",
        "spring.datasource.username=postgres",
        "spring.datasource.password=17111998",
        "spring.datasource.driver-class-name=org.postgresql.Driver",
        "spring.jpa.hibernate.ddl-auto=update"
})
public class NotificationConsumerIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private MedicineRepository medicineRepository;

    @BeforeEach
    public void setup() {
        Medicine sample = new Medicine(1L, "Panadol", "Thuốc hạ sốt giảm đau", 15000.0, 100);
        medicineRepository.save(sample);
    }

    @Test
    public void testNotificationConsumerSendsEmail() throws Exception {
        Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        KafkaTemplate<String, OrderEvent> testKafkaTemplate =
                new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));

        // Tạo sự kiện đặt hàng 5 viên Panadol (id=1, giá 15.000 -> Tổng: 75.000)
        OrderEvent orderEvent = new OrderEvent(
                "0443a2b7-c80f-4bf9-93e3-2a2f7de13659",
                "1",
                5,
                15000.0,
                75000.0,
                System.currentTimeMillis()
        );

        assertDoesNotThrow(() -> {
            testKafkaTemplate.send("medicine-stock-events", "1", orderEvent).get();
        });

        // Chờ consumer nhận và xử lý
        Thread.sleep(3000);
    }
}

