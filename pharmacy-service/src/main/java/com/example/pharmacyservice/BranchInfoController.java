package com.example.pharmacyservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pharmacy")
public class BranchInfoController {

    @Value("${app.branch-name:Chưa có thông tin chi nhánh}")
    private String branchName;

    @Value("${app.hotline:Chưa có thông tin hotline}")
    private String hotline;

    @GetMapping("/branch-info")
    public Map<String, Object> getBranchInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("branchName", branchName);
        response.put("hotline", hotline);
        return response;
    }
}

