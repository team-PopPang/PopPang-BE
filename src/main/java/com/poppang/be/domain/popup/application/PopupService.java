package com.poppang.be.domain.popup.application;

import com.poppang.be.domain.popup.dto.request.PopupImageUpsertRequestDto;
import com.poppang.be.domain.popup.dto.request.PopupRegisterRequestDto;
import com.poppang.be.domain.popup.dto.response.PopupResponseDto;
import com.poppang.be.domain.popup.entity.MediaType;
import com.poppang.be.domain.popup.entity.Popup;
import com.poppang.be.domain.popup.entity.PopupImage;
import com.poppang.be.domain.popup.entity.PopupRecommend;
import com.poppang.be.domain.popup.infrastructure.PopupImageRepository;
import com.poppang.be.domain.popup.infrastructure.PopupRecommendRepository;
import com.poppang.be.domain.popup.infrastructure.PopupRepository;
import com.poppang.be.domain.recommend.entity.Recommend;
import com.poppang.be.domain.recommend.infrastructure.RecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PopupService {

    private final PopupRepository popupRepository;
    private final PopupImageRepository popupImageRepository;
    private final RecommendRepository recommendRepository;
    private final PopupRecommendRepository popupRecommendRepository;

    public List<PopupResponseDto> getAllPopupList() {
        List<Popup> popupList = popupRepository.findAll();
        List<PopupResponseDto> popupResponseDtoList = new ArrayList<>();

        for (Popup popup : popupList) {
            popupResponseDtoList.add(PopupResponseDto.from(popup));
        }

        return popupResponseDtoList;
    }

    @Transactional
    public void registerPopup(PopupRegisterRequestDto popupRegisterRequestDto) {

        // popup 테이블 젖아
        Popup popup = Popup.builder()
                .name(popupRegisterRequestDto.getName())
                .startDate(popupRegisterRequestDto.getStartDate())
                .endDate(popupRegisterRequestDto.getEndDate())
                .openTime(popupRegisterRequestDto.getOpenTime())
                .closeTime(popupRegisterRequestDto.getCloseTime())
                .address(popupRegisterRequestDto.getAddress())
                .roadAddress(popupRegisterRequestDto.getRoadAddress())
                .longitude(popupRegisterRequestDto.getLongitude())
                .latitude(popupRegisterRequestDto.getLatitude())
                .region(popupRegisterRequestDto.getRegion())
                .geocodingQuery(popupRegisterRequestDto.getGeocodingQuery())
                .instaPostId(popupRegisterRequestDto.getInstaPostId())
                .instaPostUrl(popupRegisterRequestDto.getInstaPostUrl())
                .captionSummary(popupRegisterRequestDto.getCaptionSummary())
                .caption(popupRegisterRequestDto.getCaption())
                .mediaType(popupRegisterRequestDto.getMediaType() != null ? MediaType.valueOf(popupRegisterRequestDto.getMediaType()) : null)
                .activated(Boolean.TRUE.equals(popupRegisterRequestDto.getIsActive()))
                .build();
        popupRepository.save(popup);

        // popup 이미지 저장
        if (popupRegisterRequestDto.getImageList() != null && !popupRegisterRequestDto.getImageList().isEmpty()) {
            List<PopupImage> imageList = new ArrayList<>();
            for (int i = 0; i < popupRegisterRequestDto.getImageList().size(); i++) {
                PopupImageUpsertRequestDto image = popupRegisterRequestDto.getImageList().get(i);
                imageList.add(PopupImage.builder()
                        .popup(popup)
                        .imageUrl(image.getImageUrl())
                        .sortOrder(image.getSortOrder() != null ? image.getSortOrder() : i)
                        .build());
            }
            popupImageRepository.saveAll(imageList);
        }

        // popup 이미지 저장
        if (popupRegisterRequestDto.getRecommendIds() != null && !popupRegisterRequestDto.getRecommendIds().isEmpty()) {
            List<Recommend> found = recommendRepository.findAllById(popupRegisterRequestDto.getRecommendIds());
            if (found.size() != popupRegisterRequestDto.getRecommendIds().size()) {
                throw new IllegalArgumentException("유효하지 않은 recommendId가 포함되어 있습니다. ");
            }

            List<PopupRecommend> popupRecommendList = new ArrayList<>();
            for (Recommend recommend : found) {
                popupRecommendList.add(PopupRecommend.builder()
                        .popup(popup)
                        .recommend(recommend)
                        .build());
            }
            popupRecommendRepository.saveAll(popupRecommendList);
        }
    }

}
