package com.poppang.be.domain.popup.application;

import com.poppang.be.common.util.StringNormalizer;
import com.poppang.be.domain.favorite.infrastructure.UserFavoriteRepository;
import com.poppang.be.domain.popup.dto.response.PopupUserResponseDto;
import com.poppang.be.domain.popup.entity.Popup;
import com.poppang.be.domain.popup.entity.PopupImage;
import com.poppang.be.domain.popup.entity.PopupRecommend;
import com.poppang.be.domain.popup.enums.HomeSortStandard;
import com.poppang.be.domain.popup.enums.MapSortStandard;
import com.poppang.be.domain.popup.infrastructure.PopupImageRepository;
import com.poppang.be.domain.popup.infrastructure.PopupRecommendRepository;
import com.poppang.be.domain.popup.infrastructure.PopupRepository;
import com.poppang.be.domain.popup.infrastructure.PopupTotalViewCountRepository;
import com.poppang.be.domain.popup.mapper.PopupUserResponseDtoMapper;
import com.poppang.be.domain.recommend.infrastructure.RecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PopupUserService {

    private final PopupRepository popupRepository;
    private final PopupImageRepository popupImageRepository;
    private final RecommendRepository recommendRepository;
    private final PopupRecommendRepository popupRecommendRepository;
    private final UserFavoriteRepository userFavoriteRepository;
    private final PopupTotalViewCountRepository popupTotalViewCountRepository;
    private final PopupUserResponseDtoMapper popupUserResponseDtoMapper;

    @Transactional(readOnly = true)
    public List<PopupUserResponseDto> getAllPopupList(String userUuid) {

        List<Popup> popupList = popupRepository.findAll();
        if (popupList.isEmpty()) {
            return List.of();
        }
        // 유저가 찜한 팝업 id 리스트
        Set<Long> favoritedPopupIdList = userFavoriteRepository.findAllByUserUuid(userUuid)
                .stream()
                .map(f -> f.getPopup().getId())
                .collect(Collectors.toSet());

        return popupUserResponseDtoMapper.toPopupUserResponseDtoList(popupList, favoritedPopupIdList);
    }

    @Transactional(readOnly = true)
    public PopupUserResponseDto getPopupByUuid(String userUuid, String popupUuid) {
        Popup popup = popupRepository.findByUuid(popupUuid)
                .orElseThrow(() -> new IllegalArgumentException("팝업을 찾을 수 없습니다. "));

        // 팝업 이미지
        List<String> imageUrlList = popupImageRepository
                .findAllByPopup_IdOrderByPopup_IdAscSortOrderAsc(popup.getId())
                .stream()
                .map(PopupImage::getImageUrl)
                .toList();

        // 추천
        PopupRecommend popupRecommend = popupRecommendRepository.findFirstByPopup_Id(popup.getId());
        String recommendName = (popupRecommend != null) ? popupRecommend.getRecommend().getRecommendName() : null;

        //좋아요 수
        Long favoriteCount = userFavoriteRepository.countByPopupUuid(popup.getUuid());

        // 조회 수
        Long viewCount = popupTotalViewCountRepository.getViewCountByPopupUuid(popup.getUuid());

        // 좋아요 여부
        boolean isFavorited = userFavoriteRepository.existsByUser_UuidAndPopup_Uuid(userUuid, popupUuid);

        // DTO 조립
        PopupUserResponseDto popupUserResponseDto = PopupUserResponseDto.builder()
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
                .recommend(recommendName)
                .favoriteCount(favoriteCount)
                .viewCount(viewCount)
                .favorited(isFavorited)
                .build();

        return popupUserResponseDto;
    }

    @Transactional(readOnly = true)
    public List<PopupUserResponseDto> getUpcomingPopupList(String userUuid, Integer upcomingDays) {
        int days = (upcomingDays == null || upcomingDays <= 0) ? 10 : upcomingDays;

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(days);

        List<Popup> popupList = popupRepository.findByActivatedTrueAndStartDateBetween(startDate, endDate);

        Set<Long> favoritedPopupIdList = userFavoriteRepository.findAllByUserUuid(userUuid)
                .stream()
                .map(f -> f.getPopup().getId())
                .collect(Collectors.toSet());

        return popupUserResponseDtoMapper.toPopupUserResponseDtoList(popupList, favoritedPopupIdList);
    }

    public List<PopupUserResponseDto> getSearchPopupList(String userUuid, String q) {
        String term = (q == null ? "" : q.trim());
        if (term.isEmpty()) return List.of();

        List<Popup> popupList = popupRepository.searchActivatedByKeyword(term);

        Set<Long> favoritedPopupIdList = userFavoriteRepository.findAllByUserUuid(userUuid)
                .stream()
                .map(f -> f.getPopup().getId())
                .collect(Collectors.toSet());

        return popupUserResponseDtoMapper.toPopupUserResponseDtoList(popupList, favoritedPopupIdList);

    }

    public List<PopupUserResponseDto> getInProgressPopupList(String userUuid) {

        List<Popup> popupList = popupRepository.findInProgressPopupList();

        Set<Long> favoritedPopupIdList = userFavoriteRepository.findAllByUserUuid(userUuid)
                .stream()
                .map(f -> f.getPopup().getId())
                .collect(Collectors.toSet());

        return popupUserResponseDtoMapper.toPopupUserResponseDtoList(popupList, favoritedPopupIdList);
    }

    public List<PopupUserResponseDto> getFilteredHomePopupList(String userUuid, String region, String district, HomeSortStandard homeSortStandard) {
        String normalizedRegion = StringNormalizer.normalizeRegion(region);
        String normalizedDistrict = StringNormalizer.normalizeDistrict(district);

        Set<Long> favoritedPopupIdList = userFavoriteRepository.findAllByUserUuid(userUuid)
                .stream()
                .map(f -> f.getPopup().getId())
                .collect(Collectors.toSet());

        if (homeSortStandard == HomeSortStandard.NEWEST) {
            List<Popup> popupList = popupRepository.findActiveByNewest(normalizedRegion, normalizedDistrict);

            return popupUserResponseDtoMapper.toPopupUserResponseDtoList(popupList, favoritedPopupIdList);
        } else if (homeSortStandard == HomeSortStandard.CLOSING_SOON) {
            List<Popup> popupList = popupRepository.findActiveByClosingSoon(normalizedRegion, normalizedDistrict);

            return popupUserResponseDtoMapper.toPopupUserResponseDtoList(popupList, favoritedPopupIdList);
        } else if (homeSortStandard == HomeSortStandard.MOST_FAVORITED) {
            List<Popup> popupList = popupRepository.findActiveByMostFavorited(normalizedRegion, normalizedDistrict);

            return popupUserResponseDtoMapper.toPopupUserResponseDtoList(popupList, favoritedPopupIdList);
        } else if (homeSortStandard == HomeSortStandard.MOST_VIEWED) {
            List<Popup> popupList = popupRepository.findActiveByMostViewed(normalizedRegion, normalizedDistrict);

            return popupUserResponseDtoMapper.toPopupUserResponseDtoList(popupList, favoritedPopupIdList);
        } else {
            throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다: " + homeSortStandard);
        }

    }

    public List<PopupUserResponseDto> getFilteredMapPopupList(String userUuid, String region, String district, Double latitude, Double longitude, MapSortStandard mapSortStandard) {
        String normalizedRegion = StringNormalizer.normalizeRegion(region);
        String normalizedDistrict = StringNormalizer.normalizeDistrict(district);

        Set<Long> favoritedPopupIdList = userFavoriteRepository.findAllByUserUuid(userUuid)
                .stream()
                .map(f -> f.getPopup().getId())
                .collect(Collectors.toSet());

        if (mapSortStandard == MapSortStandard.CLOSEST) {
            List<Popup> popupList = popupRepository.findActiveByClosest(normalizedRegion, normalizedDistrict, latitude, longitude);

            return popupUserResponseDtoMapper.toPopupUserResponseDtoList(popupList, favoritedPopupIdList);
        } else if (mapSortStandard == MapSortStandard.NEWEST) {
            List<Popup> popupList = popupRepository.findActiveByNewest(normalizedRegion, normalizedDistrict);

            return popupUserResponseDtoMapper.toPopupUserResponseDtoList(popupList, favoritedPopupIdList);
        } else if (mapSortStandard == MapSortStandard.CLOSING_SOON) {
            List<Popup> popupList = popupRepository.findActiveByClosingSoon(normalizedRegion, normalizedDistrict);

            return popupUserResponseDtoMapper.toPopupUserResponseDtoList(popupList, favoritedPopupIdList);
        } else if (mapSortStandard == MapSortStandard.MOST_FAVORITED) {
            List<Popup> popupList = popupRepository.findActiveByMostViewed(normalizedRegion, normalizedDistrict);

            return popupUserResponseDtoMapper.toPopupUserResponseDtoList(popupList, favoritedPopupIdList);
        } else if (mapSortStandard == MapSortStandard.MOST_VIEWED) {
            List<Popup> popupList = popupRepository.findActiveByMostViewed(normalizedRegion, normalizedDistrict);

            return popupUserResponseDtoMapper.toPopupUserResponseDtoList(popupList, favoritedPopupIdList);
        } else {
            throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다.: " + mapSortStandard);
        }
    }

}
