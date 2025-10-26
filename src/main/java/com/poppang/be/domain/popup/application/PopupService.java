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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PopupService {

    private final PopupRepository popupRepository;
    private final PopupImageRepository popupImageRepository;
    private final RecommendRepository recommendRepository;
    private final PopupRecommendRepository popupRecommendRepository;

    public List<PopupResponseDto> getAllPopupList() {
        List<Popup> popupList = popupRepository.findAll();

        List<Long> popupIdList = new ArrayList<>();
        for (Popup popup : popupList) {
            popupIdList.add(popup.getId());
        }

        // popup 이미지 조회
        List<PopupImage> popupImageList = popupImageRepository.findAllByPopup_IdInOrderByPopup_IdAscSortOrderAsc(popupIdList);

        Map<Long, List<String>> imageMap = new HashMap<>();
        for (PopupImage popupImage : popupImageList) {
            Long popupId = popupImage.getPopup().getId();
            imageMap.computeIfAbsent(popupId, k -> new ArrayList<>())
                    .add(popupImage.getImageUrl());
        }

        // popup 추천 조회
        List<PopupRecommend> popupRecommendList = popupRecommendRepository.findAllByPopup_IdIn(popupIdList);

        Map<Long, String> recommendMap = new HashMap<>();
        for (PopupRecommend popupRecommend : popupRecommendList) {
            Long popupId = popupRecommend.getPopup().getId();
            recommendMap.putIfAbsent(popupId, popupRecommend.getRecommend().getRecommendName());
        }

        List<PopupResponseDto> popupResponseDtoList = new ArrayList<>();
        for (Popup popup : popupList) {
            List<String> imageUrlList = imageMap.getOrDefault(popup.getId(), List.of());
            String recommend = recommendMap.getOrDefault(popup.getId(), null);

            popupResponseDtoList.add(PopupResponseDto.builder()
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
                    .instaPostId(popup.getInstaPostId())
                    .instaPostUrl(popup.getInstaPostUrl())
                    .captionSummary(popup.getCaptionSummary())
                    .imageUrlList(imageUrlList)
                    .mediaType(popup.getMediaType())
                    .recommend(recommend)
                    .build());
        }

        return popupResponseDtoList;

    }

    @Transactional
    public void registerPopup(PopupRegisterRequestDto popupRegisterRequestDto) {

        // popup 테이블 저장
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
        if (popupRegisterRequestDto.getRecommendIdList() != null && !popupRegisterRequestDto.getRecommendIdList().isEmpty()) {
            List<Recommend> found = recommendRepository.findAllById(popupRegisterRequestDto.getRecommendIdList());
            if (found.size() != popupRegisterRequestDto.getRecommendIdList().size()) {
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

    @Transactional(readOnly = true)
    public List<PopupResponseDto> getSearchPopupList(String q) {
        String term = (q == null ? "" : q.trim());
        if (term.isEmpty()) return List.of();

        List<Popup> popupList = popupRepository.searchActivatedByKeyword(term);
        if (popupList.isEmpty()) return List.of();

        List<Long> popupIdList = new ArrayList<>();
        for (Popup popup : popupList) {
            popupIdList.add(popup.getId());
        }

        // popup 이미지 조회
        List<PopupImage> popupImageList = popupImageRepository.findAllByPopup_IdInOrderByPopup_IdAscSortOrderAsc(popupIdList);

        Map<Long, List<String>> imageMap = new HashMap<>();
        for (PopupImage popupImage : popupImageList) {
            Long popupId = popupImage.getPopup().getId();
            imageMap.computeIfAbsent(popupId, k -> new ArrayList<>())
                    .add(popupImage.getImageUrl());
        }

        // popup 추천 조회
        List<PopupRecommend> popupRecommendList = popupRecommendRepository.findAllByPopup_IdIn(popupIdList);

        Map<Long, String> recommendMap = new HashMap<>();
        for (PopupRecommend popupRecommend : popupRecommendList) {
            Long popupId = popupRecommend.getPopup().getId();
            recommendMap.putIfAbsent(popupId, popupRecommend.getRecommend().getRecommendName());
        }

        List<PopupResponseDto> popupResponseDtoList = new ArrayList<>();
        for (Popup popup : popupList) {
            List<String> imageUrlList = imageMap.getOrDefault(popup.getId(), List.of());
            String recommend = recommendMap.getOrDefault(popup.getId(), null);

            popupResponseDtoList.add(PopupResponseDto.builder()
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
                    .instaPostId(popup.getInstaPostId())
                    .instaPostUrl(popup.getInstaPostUrl())
                    .captionSummary(popup.getCaptionSummary())
                    .imageUrlList(imageUrlList)
                    .mediaType(popup.getMediaType())
                    .recommend(recommend)
                    .build());
        }

        return popupResponseDtoList;
    }

    public List<PopupResponseDto> getUpcomingPopupList(Integer upcomingDays) {
        int days = (upcomingDays == null || upcomingDays <= 0) ? 10 : upcomingDays;

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(days);

        List<Popup> popupList = popupRepository.findByActivatedTrueAndStartDateBetween(startDate, endDate);

        List<Long> popupIdList = new ArrayList<>();
        for (Popup popup : popupList) {
            popupIdList.add(popup.getId());
        }

        // popup 이미지 조회
        List<PopupImage> popupImageList = popupImageRepository.findAllByPopup_IdInOrderByPopup_IdAscSortOrderAsc(popupIdList);

        Map<Long, List<String>> imageMap = new HashMap<>();
        for (PopupImage popupImage : popupImageList) {
            Long popupId = popupImage.getPopup().getId();
            imageMap.computeIfAbsent(popupId, k -> new ArrayList<>())
                    .add(popupImage.getImageUrl());
        }
        // popup 추천 조회
        List<PopupRecommend> popupRecommendList = popupRecommendRepository.findAllByPopup_IdIn(popupIdList);

        Map<Long, String> recommendMap = new HashMap<>();
        for (PopupRecommend popupRecommend : popupRecommendList) {
            Long popupId = popupRecommend.getPopup().getId();
            recommendMap.putIfAbsent(popupId, popupRecommend.getRecommend().getRecommendName());
        }

        List<PopupResponseDto> popupResponseDtoList = new ArrayList<>();
        for (Popup popup : popupList) {
            List<String> imageUrlList = imageMap.getOrDefault(popup.getId(), List.of());
            String recommend = recommendMap.getOrDefault(popup.getId(), null);

            popupResponseDtoList.add(PopupResponseDto.builder()
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
                    .instaPostId(popup.getInstaPostId())
                    .instaPostUrl(popup.getInstaPostUrl())
                    .captionSummary(popup.getCaptionSummary())
                    .imageUrlList(imageUrlList)
                    .mediaType(popup.getMediaType())
                    .recommend(recommend)
                    .build());
        }

        return popupResponseDtoList;
    }

    public List<PopupResponseDto> getInProgressPopupList() {
        List<Popup> inProgressPopupList = popupRepository.findInProgressPopupList();
        List<Long> popupIdList = new ArrayList<>();
        for (Popup popup : inProgressPopupList) {
            popupIdList.add(popup.getId());
        }

        // popup 이미지 조회
        List<PopupImage> popupImageList = popupImageRepository.findAllByPopup_IdInOrderByPopup_IdAscSortOrderAsc(popupIdList);

        Map<Long, List<String>> imageMap = new HashMap<>();
        for (PopupImage popupImage : popupImageList) {
            Long popupId = popupImage.getPopup().getId();
            imageMap.computeIfAbsent(popupId, k -> new ArrayList<>())
                    .add(popupImage.getImageUrl());
        }
        // popup 추천 조회
        List<PopupRecommend> popupRecommendList = popupRecommendRepository.findAllByPopup_IdIn(popupIdList);

        Map<Long, String> recommendMap = new HashMap<>();
        for (PopupRecommend popupRecommend : popupRecommendList) {
            Long popupId = popupRecommend.getPopup().getId();
            recommendMap.putIfAbsent(popupId, popupRecommend.getRecommend().getRecommendName());
        }

        List<PopupResponseDto> popupResponseDtoList = new ArrayList<>();
        for (Popup popup : inProgressPopupList) {
            List<String> imageUrlList = imageMap.getOrDefault(popup.getId(), List.of());
            String recommend = recommendMap.getOrDefault(popup.getId(), null);

            popupResponseDtoList.add(PopupResponseDto.builder()
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
                    .instaPostId(popup.getInstaPostId())
                    .instaPostUrl(popup.getInstaPostUrl())
                    .captionSummary(popup.getCaptionSummary())
                    .imageUrlList(imageUrlList)
                    .mediaType(popup.getMediaType())
                    .recommend(recommend)
                    .build());
        }

        return popupResponseDtoList;
    }

}
