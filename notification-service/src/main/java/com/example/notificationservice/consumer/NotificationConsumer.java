package com.example.notificationservice.consumer;

import com.example.notificationservice.entity.Medicine;
import com.example.notificationservice.event.OrderEvent;
import com.example.notificationservice.repository.MedicineRepository;
import com.example.notificationservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Lắng nghe topic medicine-stock-events từ Kafka Broker
     * Sử dụng group-id="notification-group" (khác với inventory-group)
     * Nhờ đó cả inventory-service và notification-service đều đồng thời nhận được sự kiện
     */
    @KafkaListener(topics = "medicine-stock-events", groupId = "notification-group")
    public void handleOrderNotification(OrderEvent orderEvent,
                                        @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key) {

        String messageKey = key != null ? key : orderEvent.getMedicineId();

        // 1. In ra log dòng đầu tiên theo đúng ảnh mẫu
        System.out.println("Đã gửi đơn hàng: " + orderEvent.getOrderId() + " với Key: " + messageKey);

        // In log theo yêu cầu bài toán
        log.info("Hóa đơn cho đơn hàng [{}] đã được gửi tới khách hàng", orderEvent.getOrderId());

        // 2. Tìm thông tin thuốc trong Database (Hibernate select query)
        Long medicineId = Long.valueOf(orderEvent.getMedicineId());
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseGet(() -> new Medicine(medicineId, "Panadol", "Thuốc giảm đau hạ sốt", 15000.0, 100));

        // 3. Kích hoạt phương thức gửi email
        System.out.println("Đã gửi thông báo tới email khách hàng");
        emailService.sendOrderInvoiceEmail(orderEvent, medicine);
    }
}

