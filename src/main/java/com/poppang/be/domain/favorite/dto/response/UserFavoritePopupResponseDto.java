package com.poppang.be.domain.favorite.dto.response;

import com.poppang.be.domain.popup.entity.MediaType;
import com.poppang.be.domain.popup.entity.Popup;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

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
    private String imageUrl;
    private MediaType mediaType;
    private String errorCode;

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
                                        String imageUrl,
                                        MediaType mediaType,
                                        String errorCode) {
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
        this.imageUrl = imageUrl;
        this.mediaType = mediaType;
        this.errorCode = errorCode;
    }

    public static UserFavoritePopupResponseDto from(Popup popup) {
        return UserFavoritePopupResponseDto.builder()
                .id(popup.getId())
                .popupUuid(popup.getUuid())
                .name(popup.getName())
                .startDate(popup.getStartDate())
                .endDate(popup.getEndDate())
                .openTime(popup.getOpenTime())
                .closeTime(popup.getCloseTime())
                .address(popup.getAddress())
                .roadAddress(popup.getRoadAddress())
                .region(popup.getRegion())
                .latitude(popup.getLatitude())
                .longitude(popup.getLongitude())
                .geocodingQuery(popup.getGeocodingQuery())
                .instaPostId(popup.getInstaPostId())
                .instaPostUrl(popup.getInstaPostUrl())
                .likeCount(popup.getLikeCount())
                .captionSummary(popup.getCaptionSummary())
                .caption(popup.getCaption())
                .imageUrl(popup.getImageUrl())
                .mediaType(popup.getMediaType())
                .errorCode(popup.getErrorCode())
                .build();
    }

}
