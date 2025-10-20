package com.poppang.be.domain.favorite.dto.response;

import com.poppang.be.domain.popup.entity.MediaType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class UserFavoritePopupResponseDto {

    private Long id;
    private String popupUuid;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String address;
    private String roadAddress;
    private String region;
    private Double latitude;
    private Double longitude;
    private String geocodingQuery;
    private String instaPostId;
    private String instaPostUrl;
    private int likeCount;
    private String captionSummary;
    private String caption;

    private List<String> imageUrlList;
    private MediaType mediaType;
    private String errorCode;
    private String recommend;

    @Builder
    public UserFavoritePopupResponseDto(Long id,
                                        String popupUuid,
                                        String name,
                                        LocalDate startDate,
                                        LocalDate endDate,
                                        LocalTime openTime,
                                        LocalTime closeTime,
                                        String address,
                                        String roadAddress,
                                        String region,
                                        Double latitude,
                                        Double longitude,
                                        String geocodingQuery,
                                        String instaPostId,
                                        String instaPostUrl,
                                        int likeCount,
                                        String captionSummary,
                                        String caption,
                                        List<String> imageUrlList,
                                        MediaType mediaType,
                                        String errorCode,
                                        String recommend) {
        this.id = id;
        this.popupUuid = popupUuid;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.address = address;
        this.roadAddress = roadAddress;
        this.region = region;
        this.latitude = latitude;
        this.longitude = longitude;
        this.geocodingQuery = geocodingQuery;
        this.instaPostId = instaPostId;
        this.instaPostUrl = instaPostUrl;
        this.likeCount = likeCount;
        this.captionSummary = captionSummary;
        this.caption = caption;
        this.imageUrlList = imageUrlList;
        this.mediaType = mediaType;
        this.errorCode = errorCode;
        this.recommend = recommend;
    }

}
