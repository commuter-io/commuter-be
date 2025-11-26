package org.example.backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.service.SubwayService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final SubwayService subwayService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("애플리케이션 시작... 초기 지하철 공지 데이터를 적재합니다.");
        try {
            // 서버 시작 시 최근 50개의 공지사항을 가져와 DB에 저장
            subwayService.fetchAndSaveNotices(1, 50);
            log.info("초기 데이터 적재 완료.");
        } catch (Exception e) {
            log.error("초기 데이터 적재 중 오류가 발생했습니다.", e);
        }
    }
}
