package com.poppang.be.domain.favorite.application;

import com.poppang.be.domain.favorite.dto.request.UserFavoriteDeleteRequestDto;
import com.poppang.be.domain.favorite.dto.request.UserFavoriteRegisterRequestDto;
import com.poppang.be.domain.favorite.dto.response.FavoriteCountResponseDto;
import com.poppang.be.domain.favorite.entity.UserFavorite;
import com.poppang.be.domain.favorite.infrastructure.UserFavoriteRepository;
import com.poppang.be.domain.popup.dto.response.PopupResponseDto;
import com.poppang.be.domain.popup.entity.Popup;
import com.poppang.be.domain.popup.entity.PopupImage;
import com.poppang.be.domain.popup.entity.PopupRecommend;
import com.poppang.be.domain.popup.infrastructure.PopupImageRepository;
import com.poppang.be.domain.popup.infrastructure.PopupRecommendRepository;
import com.poppang.be.domain.popup.infrastructure.PopupRepository;
import com.poppang.be.domain.popup.infrastructure.PopupTotalViewCountRepository;
import com.poppang.be.domain.users.entity.Users;
import com.poppang.be.domain.users.infrastructure.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserFavoriteService {

    private final UsersRepository usersRepository;
    private final PopupRepository popupRepository;
    private final UserFavoriteRepository userFavoriteRepository;
    private final PopupImageRepository popupImageRepository;
    private final PopupRecommendRepository popupRecommendRepository;
    private final PopupTotalViewCountRepository popupTotalViewCountRepository;


    @Transactional
    public void registerFavorite(UserFavoriteRegisterRequestDto userFavoriteRegisterRequestDto) {
        Users user = usersRepository.findByUuid(userFavoriteRegisterRequestDto.getUserUuid())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. "));

        Popup popup = popupRepository.findByUuid(userFavoriteRegisterRequestDto.getPopupUuid())
                .orElseThrow(() -> new IllegalArgumentException("팝업을 찾을 수 없습니다. "));

        boolean exists = userFavoriteRepository.existsByUserAndPopup(user, popup);
        if (exists) {
            throw new IllegalStateException("이미 찜한 팝업입니다. ");
        }

        UserFavorite userFavorite = new UserFavorite(user, popup);

        userFavoriteRepository.save(userFavorite);
    }

    @Transactional
    public void deleteFavorite(UserFavoriteDeleteRequestDto userFavoriteDeleteRequestDto) {
        UserFavorite userFavorite = userFavoriteRepository.findByUserUuidAndPopupUuid(userFavoriteDeleteRequestDto.getUserUuid(), userFavoriteDeleteRequestDto.getPopupUuid())
                .orElseThrow(() -> new IllegalStateException("해당 찜 기록이 없습니다. "));

        userFavoriteRepository.delete(userFavorite);
    }

    @Transactional(readOnly = true)
    public FavoriteCountResponseDto getFavoriteCount(String popupUuid) {
        long count = userFavoriteRepository.countByPopupUuid(popupUuid);

        return FavoriteCountResponseDto.from(count);
    }

    public List<PopupResponseDto> getFavoritePopupList(String userUuid) {
        Users user = usersRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        List<UserFavorite> userFavoriteList = userFavoriteRepository.findAllByUserUuid(userUuid);

        List<Long> popupIdList = userFavoriteList.stream()
                .map(userFavorite -> userFavorite.getPopup().getId())
                .toList();

        List<Popup> popupList = popupRepository.findAllById(popupIdList);
        List<String> popupUuidList = popupRepository.findAllUuidByIdIn(popupIdList);

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
}
