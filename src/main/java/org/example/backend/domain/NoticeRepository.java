package org.example.backend.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long>, JpaSpecificationExecutor<Notice> { // JpaSpecificationExecutor 추가

    // 특정 호선을 포함하고, 특정 문제 유형에 해당하는 공지를 찾음
    List<Notice> findByLineListContainingAndIncidentType(String line, IncidentType incidentType);

    // 특정 호선을 포함하는 모든 공지를 찾음
    List<Notice> findByLineListContaining(String line);

    // 특정 문제 유형에 해당하는 모든 공지를 찾음
    List<Notice> findByIncidentType(IncidentType incidentType);

    // 제목과 시작일로 공지를 찾음 (중복 저장 방지용)
    Optional<Notice> findByNoticeTitleAndStartDate(String noticeTitle, String startDate);
}
