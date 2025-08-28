package org.example.backend.service;

import org.springframework.stereotype.Service;

@Service
public class ApiService {
    public String getHelloMessage() {
        return "Hello from Spring Boot!";
    }
}
