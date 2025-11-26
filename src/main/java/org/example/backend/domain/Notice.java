package org.example.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String noticeTitle; // noftTtl

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String noticeContent; // noftCn

    @Column
    private String lineList; // lineNmLst

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentType incidentType; // noftTtl 기반으로 분류

    @Column
    private String startDate; // xcseSitnBgngDt

    @Column
    private String endDate; // xcseSitnEndDt

    // 새로 추가되는 필드들
    @Column
    private String stationCodeList; // stnSctnCdLst

    @Column(length = 1)
    private String nonstopYn; // nonstopYn

    @Column
    private String noticeCode; // noftSeCd

    @Column
    private String createdDate; // crtrYmd

    @Column
    private String occurredDate; // noftOcrnDt

    @Column
    private String upDownBound; // upbdnbSe
}
