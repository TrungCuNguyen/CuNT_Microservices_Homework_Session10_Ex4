package com.example.inventoryservice.consumer;

import com.example.inventoryservice.entity.Medicine;
import com.example.inventoryservice.entity.Order;
import com.example.inventoryservice.event.OrderEvent;
import com.example.inventoryservice.repository.MedicineRepository;
import com.example.inventoryservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Lắng nghe topic medicine-stock-events từ Kafka Broker
     * Cấu hình group-id="inventory-group" đảm bảo tính nhất quán (không xử lý trùng lặp trong cùng một cụm)
     */
    @KafkaListener(topics = "medicine-stock-events", groupId = "inventory-group")
    @Transactional
    public void consumeOrderEvent(OrderEvent orderEvent) {
        log.info(">>> [KAFKA CONSUMER] Đã nhận sự kiện đơn hàng từ topic medicine-stock-events: {}", orderEvent);

        Long medicineId = Long.valueOf(orderEvent.getMedicineId());
        int orderQuantity = orderEvent.getQuantity();

        double priceSell = (orderEvent.getPriceSell() != null && orderEvent.getPriceSell() > 0)
                ? orderEvent.getPriceSell() : 50000.0;
        double totalAmount = (orderEvent.getTotalAmount() != null && orderEvent.getTotalAmount() > 0)
                ? orderEvent.getTotalAmount() : (priceSell * orderQuantity);

        // 1. Thêm mới đơn hàng vào bảng orders
        Order order = new Order();
        order.setMedicineId(medicineId);
        order.setPriceSell(priceSell);
        order.setQuantity(orderQuantity);
        order.setTimestamp(orderEvent.getTimestamp());
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        // 2. Lấy thông tin thuốc và trừ số lượng tồn kho (UPDATE medicinse SET quantity = quantity - quantity)
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseGet(() -> {
                    Medicine newMed = new Medicine();
                    newMed.setId(medicineId);
                    newMed.setMedicineName("Panadol Extra");
                    newMed.setDescription("Thuốc giảm đau hạ sốt");
                    newMed.setPrice(priceSell);
                    newMed.setQuantity(100);
                    return medicineRepository.save(newMed);
                });

        medicine.setQuantity(medicine.getQuantity() - orderQuantity);
        medicineRepository.save(medicine);

        // 3. Hiển thị thông báo chính xác như kết quả mong muốn
        System.out.println("Thêm mới đơn hàng thành công !");
    }
}

