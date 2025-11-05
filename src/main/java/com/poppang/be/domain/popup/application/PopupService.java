package com.poppang.be.domain.popup.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poppang.be.common.util.StringNormalizer;
import com.poppang.be.domain.favorite.infrastructure.UserFavoriteRepository;
import com.poppang.be.domain.popup.dto.request.PopupImageUpsertRequestDto;
import com.poppang.be.domain.popup.dto.request.PopupRegisterRequestDto;
import com.poppang.be.domain.popup.dto.response.PopupResponseDto;
import com.poppang.be.domain.popup.dto.response.RegionDistrictsResponse;
import com.poppang.be.domain.popup.entity.*;
import com.poppang.be.domain.popup.infrastructure.PopupImageRepository;
import com.poppang.be.domain.popup.infrastructure.PopupRecommendRepository;
import com.poppang.be.domain.popup.infrastructure.PopupRepository;
import com.poppang.be.domain.popup.infrastructure.PopupTotalViewCountRepository;
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
    private final UserFavoriteRepository userFavoriteRepository;
    private final PopupTotalViewCountRepository popupTotalViewCountRepository;
    private final ObjectMapper objectMapper;

    public List<PopupResponseDto> getAllPopupList() {
        List<Popup> popupList = popupRepository.findAll();

        // id/uuid 수집
        List<Long> popupIdList = new ArrayList<>(popupList.size());
        List<String> popupUuidList = new ArrayList<>(popupList.size());
        for (Popup popup : popupList) {
            popupIdList.add(popup.getId());
            popupUuidList.add(popup.getUuid());
        }

        // 팝업 이미지
        List<PopupImage> images = popupImageRepository.findAllByPopup_IdInOrderByPopup_IdAscSortOrderAsc(popupIdList);
        Map<Long, List<String>> imageMap = new HashMap<>();
        for (PopupImage img : images) {
            imageMap.computeIfAbsent(img.getPopup().getId(), k -> new ArrayList<>()).add(img.getImageUrl());
        }

        // 추천
        List<PopupRecommend> recs = popupRecommendRepository.findAllByPopup_IdIn(popupIdList);
        Map<Long, String> recommendMap = new HashMap<>();
        for (PopupRecommend r : recs) {
            recommendMap.putIfAbsent(r.getPopup().getId(), r.getRecommend().getRecommendName());
        }

        // 좋아요 수 배치
        Map<Long, Long> favoriteCountMap = new HashMap<>();
        for (var row : userFavoriteRepository.countAllByPopupIds(popupIdList)) {
            favoriteCountMap.put(row.getPopupId(), row.getCnt());
        }

        // 조회수 배치
        Map<String, Long> viewCountMap = new HashMap<>();
        for (var row : popupTotalViewCountRepository.findAllViewCounts(popupUuidList)) {
            viewCountMap.put(row.getPopupUuid(),
                    row.getViewCount() == null ? 0L : row.getViewCount());
        }

        // DTO 조립
        List<PopupResponseDto> popupResponseDtoList = new ArrayList<>(popupList.size());
        for (Popup popup : popupList) {
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
                    .imageUrlList(imageMap.getOrDefault(popup.getId(), List.of()))
                    .mediaType(popup.getMediaType())
                    .recommend(recommendMap.getOrDefault(popup.getId(), null))
                    .favoriteCount(favoriteCountMap.getOrDefault(popup.getId(), 0L))
                    .viewCount(viewCountMap.getOrDefault(popup.getUuid(), 0L))
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
        List<String> popupUuidList = new ArrayList<>();
        for (Popup popup : popupList) {
            popupIdList.add(popup.getId());
            popupUuidList.add(popup.getUuid());
        }

        // 팝업 이미지
        List<PopupImage> images = popupImageRepository.findAllByPopup_IdInOrderByPopup_IdAscSortOrderAsc(popupIdList);
        Map<Long, List<String>> imageMap = new HashMap<>();
        for (PopupImage img : images) {
            imageMap.computeIfAbsent(img.getPopup().getId(), k -> new ArrayList<>()).add(img.getImageUrl());
        }

        // 추천
        List<PopupRecommend> recs = popupRecommendRepository.findAllByPopup_IdIn(popupIdList);
        Map<Long, String> recommendMap = new HashMap<>();
        for (PopupRecommend r : recs) {
            recommendMap.putIfAbsent(r.getPopup().getId(), r.getRecommend().getRecommendName());
        }

        // 좋아요 수 배치
        Map<Long, Long> favoriteCountMap = new HashMap<>();
        for (var row : userFavoriteRepository.countAllByPopupIds(popupIdList)) {
            favoriteCountMap.put(row.getPopupId(), row.getCnt());
        }

        // 조회수 배치
        Map<String, Long> viewCountMap = new HashMap<>();
        for (var row : popupTotalViewCountRepository.findAllViewCounts(popupUuidList)) {
            viewCountMap.put(row.getPopupUuid(),
                    row.getViewCount() == null ? 0L : row.getViewCount());
        }

        // DTO 조립
        List<PopupResponseDto> popupResponseDtoList = new ArrayList<>(popupList.size());
        for (Popup popup : popupList) {
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
                    .imageUrlList(imageMap.getOrDefault(popup.getId(), List.of()))
                    .mediaType(popup.getMediaType())
                    .recommend(recommendMap.getOrDefault(popup.getId(), null))
                    .favoriteCount(favoriteCountMap.getOrDefault(popup.getId(), 0L))
                    .viewCount(viewCountMap.getOrDefault(popup.getUuid(), 0L))
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
        List<String> popupUuidList = new ArrayList<>();
        for (Popup popup : popupList) {
            popupIdList.add(popup.getId());
            popupUuidList.add(popup.getUuid());
        }

        // 팝업 이미지
        List<PopupImage> images = popupImageRepository.findAllByPopup_IdInOrderByPopup_IdAscSortOrderAsc(popupIdList);
        Map<Long, List<String>> imageMap = new HashMap<>();
        for (PopupImage img : images) {
            imageMap.computeIfAbsent(img.getPopup().getId(), k -> new ArrayList<>()).add(img.getImageUrl());
        }

        // 추천
        List<PopupRecommend> recs = popupRecommendRepository.findAllByPopup_IdIn(popupIdList);
        Map<Long, String> recommendMap = new HashMap<>();
        for (PopupRecommend r : recs) {
            recommendMap.putIfAbsent(r.getPopup().getId(), r.getRecommend().getRecommendName());
        }

        // 좋아요 수 배치
        Map<Long, Long> favoriteCountMap = new HashMap<>();
        for (var row : userFavoriteRepository.countAllByPopupIds(popupIdList)) {
            favoriteCountMap.put(row.getPopupId(), row.getCnt());
        }

        // 조회수 배치
        Map<String, Long> viewCountMap = new HashMap<>();
        for (var row : popupTotalViewCountRepository.findAllViewCounts(popupUuidList)) {
            viewCountMap.put(row.getPopupUuid(),
                    row.getViewCount() == null ? 0L : row.getViewCount());
        }

        // DTO 조립
        List<PopupResponseDto> popupResponseDtoList = new ArrayList<>(popupList.size());
        for (Popup popup : popupList) {
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
                    .imageUrlList(imageMap.getOrDefault(popup.getId(), List.of()))
                    .mediaType(popup.getMediaType())
                    .recommend(recommendMap.getOrDefault(popup.getId(), null))
                    .favoriteCount(favoriteCountMap.getOrDefault(popup.getId(), 0L))
                    .viewCount(viewCountMap.getOrDefault(popup.getUuid(), 0L))
                    .build());
        }
        return popupResponseDtoList;

    }

    public List<PopupResponseDto> getInProgressPopupList() {
        List<Popup> popupList = popupRepository.findInProgressPopupList();
        List<Long> popupIdList = new ArrayList<>();
        List<String> popupUuidList = new ArrayList<>();
        for (Popup popup : popupList) {
            popupIdList.add(popup.getId());
            popupUuidList.add(popup.getUuid());
        }

        // 팝업 이미지
        List<PopupImage> images = popupImageRepository.findAllByPopup_IdInOrderByPopup_IdAscSortOrderAsc(popupIdList);
        Map<Long, List<String>> imageMap = new HashMap<>();
        for (PopupImage img : images) {
            imageMap.computeIfAbsent(img.getPopup().getId(), k -> new ArrayList<>()).add(img.getImageUrl());
        }

        // 추천
        List<PopupRecommend> recs = popupRecommendRepository.findAllByPopup_IdIn(popupIdList);
        Map<Long, String> recommendMap = new HashMap<>();
        for (PopupRecommend r : recs) {
            recommendMap.putIfAbsent(r.getPopup().getId(), r.getRecommend().getRecommendName());
        }

        // 좋아요 수 배치
        Map<Long, Long> favoriteCountMap = new HashMap<>();
        for (var row : userFavoriteRepository.countAllByPopupIds(popupIdList)) {
            favoriteCountMap.put(row.getPopupId(), row.getCnt());
        }

        // 조회수 배치
        Map<String, Long> viewCountMap = new HashMap<>();
        for (var row : popupTotalViewCountRepository.findAllViewCounts(popupUuidList)) {
            viewCountMap.put(row.getPopupUuid(),
                    row.getViewCount() == null ? 0L : row.getViewCount());
        }

        // DTO 조립
        List<PopupResponseDto> popupResponseDtoList = new ArrayList<>(popupList.size());
        for (Popup popup : popupList) {
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
                    .imageUrlList(imageMap.getOrDefault(popup.getId(), List.of()))
                    .mediaType(popup.getMediaType())
                    .recommend(recommendMap.getOrDefault(popup.getId(), null))
                    .favoriteCount(favoriteCountMap.getOrDefault(popup.getId(), 0L))
                    .viewCount(viewCountMap.getOrDefault(popup.getUuid(), 0L))
                    .build());
        }
        
        return popupResponseDtoList;
    }

    @Transactional(readOnly = true)
    public List<RegionDistrictsResponse> getRegionDistricts() {
        List<PopupRepository.RegionDistrictsRaw> rawList = popupRepository.findRegionDistrictsJson();
        List<RegionDistrictsResponse> regionDistrictsResponseList = new ArrayList<>();

        for (PopupRepository.RegionDistrictsRaw regionDistrictsRaw : rawList) {
            try {
                List<String> districts = objectMapper.readValue(
                        regionDistrictsRaw.getDistricts(),
                        new TypeReference<List<String>>() {
                        }
                );

                RegionDistrictsResponse regionDistrictsResponse = RegionDistrictsResponse.builder()
                        .region(regionDistrictsRaw.getRegion())
                        .districtList(districts)
                        .build();

                regionDistrictsResponseList.add(regionDistrictsResponse);
            } catch (Exception e) {
                throw new RuntimeException("지역/구 JSON 파싱 오류: " + regionDistrictsRaw.getDistricts(), e);
            }
        }

        return regionDistrictsResponseList;
    }

    @Transactional(readOnly = true)
    public List<PopupResponseDto> getAllPopupListNew() {
        List<Popup> popupList = popupRepository.findAll();

        // id/uuid 수집
        List<Long> popupIdList = new ArrayList<>(popupList.size());
        List<String> popupUuidList = new ArrayList<>(popupList.size());
        for (Popup p : popupList) {
            popupIdList.add(p.getId());
            popupUuidList.add(p.getUuid());
        }

        // 팝업 이미지
        List<PopupImage> images = popupImageRepository.findAllByPopup_IdInOrderByPopup_IdAscSortOrderAsc(popupIdList);
        Map<Long, List<String>> imageMap = new HashMap<>();
        for (PopupImage img : images) {
            imageMap.computeIfAbsent(img.getPopup().getId(), k -> new ArrayList<>()).add(img.getImageUrl());
        }

        // 추천
        List<PopupRecommend> recs = popupRecommendRepository.findAllByPopup_IdIn(popupIdList);
        Map<Long, String> recommendMap = new HashMap<>();
        for (PopupRecommend r : recs) {
            recommendMap.putIfAbsent(r.getPopup().getId(), r.getRecommend().getRecommendName());
        }

        // 좋아요 수 배치
        Map<Long, Long> favoriteCountMap = new HashMap<>();
        for (var row : userFavoriteRepository.countAllByPopupIds(popupIdList)) {
            favoriteCountMap.put(row.getPopupId(), row.getCnt());
        }

        // 조회수 배치
        Map<String, Long> viewCountMap = new HashMap<>();
        for (var row : popupTotalViewCountRepository.findAllViewCounts(popupUuidList)) {
            viewCountMap.put(row.getPopupUuid(),
                    row.getViewCount() == null ? 0L : row.getViewCount());
        }

        // DTO 조립
        List<PopupResponseDto> dto = new ArrayList<>(popupList.size());
        for (Popup popup : popupList) {
            dto.add(PopupResponseDto.builder()
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
                    .imageUrlList(imageMap.getOrDefault(popup.getId(), List.of()))
                    .mediaType(popup.getMediaType())
                    .recommend(recommendMap.getOrDefault(popup.getId(), null))
                    .favoriteCount(favoriteCountMap.getOrDefault(popup.getId(), 0L))
                    .viewCount(viewCountMap.getOrDefault(popup.getUuid(), 0L))
                    .build());
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public List<PopupResponseDto> getFilteredPopupList(String region, String district, SortStandard sortStandard, Double latitude, Double longitude) {
        String normalizedDistrict = StringNormalizer.normalizeDistrict(district);

        if (sortStandard == SortStandard.LIKES) {
            // region + district + 좋아요 수 기준 정렬
            List<Popup> popupList = popupRepository.findPopupListByRegionAndLikes(region, normalizedDistrict);
            // id/uuid 수집
            List<Long> popupIdList = new ArrayList<>(popupList.size());
            List<String> popupUuidList = new ArrayList<>(popupList.size());
            for (Popup p : popupList) {
                popupIdList.add(p.getId());
                popupUuidList.add(p.getUuid());
            }

            // 팝업 이미지
            List<PopupImage> images = popupImageRepository.findAllByPopup_IdInOrderByPopup_IdAscSortOrderAsc(popupIdList);
            Map<Long, List<String>> imageMap = new HashMap<>();
            for (PopupImage img : images) {
                imageMap.computeIfAbsent(img.getPopup().getId(), k -> new ArrayList<>()).add(img.getImageUrl());
            }
            System.out.println("imageMap = " + imageMap);

            // 추천
            List<PopupRecommend> recs = popupRecommendRepository.findAllByPopup_IdIn(popupIdList);
            Map<Long, String> recommendMap = new HashMap<>();
            for (PopupRecommend r : recs) {
                recommendMap.putIfAbsent(r.getPopup().getId(), r.getRecommend().getRecommendName());
            }

            // 좋아요 수 배치
            Map<Long, Long> favoriteCountMap = new HashMap<>();
            for (var row : userFavoriteRepository.countAllByPopupIds(popupIdList)) {
                favoriteCountMap.put(row.getPopupId(), row.getCnt());
            }

            // 조회수 배치
            Map<String, Long> viewCountMap = new HashMap<>();
            for (var row : popupTotalViewCountRepository.findAllViewCounts(popupUuidList)) {
                viewCountMap.put(row.getPopupUuid(),
                        row.getViewCount() == null ? 0L : row.getViewCount());
            }

            // DTO 조립
            List<PopupResponseDto> dto = new ArrayList<>(popupList.size());
            for (Popup popup : popupList) {
                dto.add(PopupResponseDto.builder()
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
                        .imageUrlList(imageMap.getOrDefault(popup.getId(), List.of()))
                        .mediaType(popup.getMediaType())
                        .recommend(recommendMap.getOrDefault(popup.getId(), null))
                        .favoriteCount(favoriteCountMap.getOrDefault(popup.getId(), 0L))
                        .viewCount(viewCountMap.getOrDefault(popup.getUuid(), 0L))
                        .build());
            }
            return dto;

        }else{
            // region + district + 위.경도로 가까운 순 정렬
            List<Popup> popupList = popupRepository.findPopupListByRegionAndDistance(region, normalizedDistrict, latitude, longitude);
            // id/uuid 수집
            List<Long> popupIdList = new ArrayList<>(popupList.size());
            List<String> popupUuidList = new ArrayList<>(popupList.size());
            for (Popup p : popupList) {
                popupIdList.add(p.getId());
                popupUuidList.add(p.getUuid());
            }

            // 팝업 이미지
            List<PopupImage> images = popupImageRepository.findAllByPopup_IdInOrderByPopup_IdAscSortOrderAsc(popupIdList);
            Map<Long, List<String>> imageMap = new HashMap<>();
            for (PopupImage img : images) {
                imageMap.computeIfAbsent(img.getPopup().getId(), k -> new ArrayList<>()).add(img.getImageUrl());
            }

            // 추천
            List<PopupRecommend> recs = popupRecommendRepository.findAllByPopup_IdIn(popupIdList);
            Map<Long, String> recommendMap = new HashMap<>();
            for (PopupRecommend r : recs) {
                recommendMap.putIfAbsent(r.getPopup().getId(), r.getRecommend().getRecommendName());
            }

            // 좋아요 수 배치
            Map<Long, Long> favoriteCountMap = new HashMap<>();
            for (var row : userFavoriteRepository.countAllByPopupIds(popupIdList)) {
                favoriteCountMap.put(row.getPopupId(), row.getCnt());
            }

            // 조회수 배치
            Map<String, Long> viewCountMap = new HashMap<>();
            for (var row : popupTotalViewCountRepository.findAllViewCounts(popupUuidList)) {
                viewCountMap.put(row.getPopupUuid(),
                        row.getViewCount() == null ? 0L : row.getViewCount());
            }

            // DTO 조립
            List<PopupResponseDto> dto = new ArrayList<>(popupList.size());
            for (Popup popup : popupList) {
                dto.add(PopupResponseDto.builder()
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
                        .imageUrlList(imageMap.getOrDefault(popup.getId(), List.of()))
                        .mediaType(popup.getMediaType())
                        .recommend(recommendMap.getOrDefault(popup.getId(), null))
                        .favoriteCount(favoriteCountMap.getOrDefault(popup.getId(), 0L))
                        .viewCount(viewCountMap.getOrDefault(popup.getUuid(), 0L))
                        .build());
            }
            return dto;
        }

    }
}
