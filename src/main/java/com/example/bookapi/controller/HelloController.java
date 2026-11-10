package com.example.bookapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HelloController {

    // GET /  -> quick check that the server is running
    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "status", "UP",
                "message", "Book API is running",
                "time", LocalDateTime.now().toString()
        );
    }

    // GET /api/hello?name=Rahul
    @GetMapping("/api/hello")
    public Map<String, String> hello(@RequestParam(defaultValue = "World") String name) {
        return Map.of("message", "Hello, " + name + "!");
    }
}
