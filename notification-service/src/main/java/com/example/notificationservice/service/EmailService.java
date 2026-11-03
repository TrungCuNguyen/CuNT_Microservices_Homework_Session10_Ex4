package com.example.notificationservice.service;

import com.example.notificationservice.entity.Medicine;
import com.example.notificationservice.event.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    /**
     * Phương thức gửi email thông báo hóa đơn cho khách hàng
     * Hiển thị nội dung email chính xác theo mẫu bài tập
     */
    public void sendOrderInvoiceEmail(OrderEvent event, Medicine medicine) {
        String medicineName = medicine != null ? medicine.getMedicineName() : "Thuốc";
        int quantity = event.getQuantity();

        double unitPrice = (event.getPriceSell() != null && event.getPriceSell() > 0)
                ? event.getPriceSell()
                : (medicine != null ? medicine.getPrice() : 15000.0);

        double totalAmount = (event.getTotalAmount() != null && event.getTotalAmount() > 0)
                ? event.getTotalAmount()
                : (unitPrice * quantity);

        // Định dạng nội dung email như trên ảnh bài tập
        String emailContent = String.format(
                "Cảm ơn bạn đã đặt hàng tại Pharma Medicine:\n" +
                "Tên thuốc : %s\n" +
                "Số lượng : %d\n" +
                "Tổng tiền : %.6f",
                medicineName,
                quantity,
                totalAmount
        );

        System.out.println(emailContent);
    }
}

