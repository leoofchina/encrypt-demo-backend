package net.zhi365.encryptdemo.controller;

import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@CrossOrigin("*")
@RestController
public class DemoController {

    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody DemoForm form) {
        return Map.of(
                "echo", form,
                "receivedAt", Instant.now().toString()
        );
    }

    @GetMapping("/health")
    public String health() {
        return "ok";
    }

    @Data
    public static class DemoForm {
        private String message;
    }
}