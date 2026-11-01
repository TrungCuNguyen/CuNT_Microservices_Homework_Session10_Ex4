package com.example.pharmacyservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BranchInfoPrinter implements CommandLineRunner {

    @Value("${app.branch-name:Chưa có thông tin chi nhánh}")
    private String branchName;

    @Value("${app.hotline:Chưa có thông tin hotline}")
    private String hotline;

    @Override
    public void run(String... args) {
        System.out.println("==================================================================");
        System.out.println(">>> [PHARMACY-SERVICE] KHỞI ĐỘNG THÀNH CÔNG");
        System.out.println(">>> Tên chi nhánh (app.branch-name) : " + branchName);
        System.out.println(">>> Hotline chi nhánh (app.hotline) : " + hotline);
        System.out.println("==================================================================");
    }
}

