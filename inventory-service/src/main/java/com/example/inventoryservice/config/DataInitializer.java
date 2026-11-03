package com.example.inventoryservice.config;

import com.example.inventoryservice.entity.Medicine;
import com.example.inventoryservice.repository.MedicineRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(MedicineRepository medicineRepository) {
        return args -> {
            if (!medicineRepository.existsById(1L)) {
                Medicine sample = new Medicine(
                        1L,
                        "Panadol Extra",
                        "Thuốc giảm đau hạ sốt",
                        50000.0,
                        100
                );
                medicineRepository.save(sample);
                System.out.println(">>> [INIT DATA] Đã khởi tạo thuốc mẫu id=1 (Panadol Extra, Tồn kho ban đầu: 100)");
            }
        };
    }
}

