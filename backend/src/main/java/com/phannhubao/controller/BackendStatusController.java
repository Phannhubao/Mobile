package com.phannhubao.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class BackendStatusController {

    @GetMapping("/")
    public Map<String, String> getStatus() {
        return Map.of(
                "application", "phannhubao",
                "status", "running"
        );
    }
}
