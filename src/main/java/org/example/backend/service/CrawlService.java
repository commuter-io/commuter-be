package org.example.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CrawlService {
    private final WebClient webClient;

    public CrawlService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8000") // FastAPI 서버 주소
                .build();
    }

    public String getCrawlResult(String query) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/crawl")
                        .queryParam("q", query) // 예시: ?q=스프링부트
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("FastAPI 호출 실패: " + e.getMessage()))
                .block(); // 동기 방식 (간단히 테스트용)
    }
}
