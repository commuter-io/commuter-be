package org.example.backend.controller;

import org.example.backend.service.ApiService;
import org.example.backend.service.CrawlService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final CrawlService crawlService;

    public ApiController(CrawlService crawlService) {
        this.crawlService = crawlService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }

    @GetMapping("/crawl")
    public String crawl(@RequestParam String q) {
        return crawlService.getCrawlResult(q);
    }
}
