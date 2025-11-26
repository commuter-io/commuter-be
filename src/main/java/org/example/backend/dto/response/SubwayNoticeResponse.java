package org.example.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SubwayNoticeResponse {

    @JsonProperty("response")
    private ResponseData response;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseData {
        @JsonProperty("header")
        private Header header;

        @JsonProperty("body")
        private Body body;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Header {
        @JsonProperty("resultCode")
        private String resultCode;

        @JsonProperty("resultMsg")
        private String resultMsg;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Body {
        @JsonProperty("items")
        private Items items;

        @JsonProperty("pageNo")
        private int pageNo;

        @JsonProperty("numOfRows")
        private int numOfRows;

        @JsonProperty("totalCount")
        private int totalCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Items {
        @JsonProperty("item")
        private List<NoticeItem> item;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class NoticeItem {
        @JsonProperty("noftTtl")
        private String noticeTitle;

        @JsonProperty("noftCn")
        private String noticeContent;

        @JsonProperty("noftOcrnDt")
        private String occurredDate;

        @JsonProperty("lineNmLst")
        private String lineList;

        @JsonProperty("stnSctnCdLst")
        private String stationCodeList;

        @JsonProperty("crtrYmd")
        private String createdDate;

        @JsonProperty("noftSeCd")
        private String noticeCode;

        @JsonProperty("nonstopYn")
        private String nonstopYn;

        @JsonProperty("upbdnbSe")
        private String upDownBound;

        @JsonProperty("xcseSitnBgngDt")
        private String startDate;

        @JsonProperty("xcseSitnEndDt")
        private String endDate;
    }
}
