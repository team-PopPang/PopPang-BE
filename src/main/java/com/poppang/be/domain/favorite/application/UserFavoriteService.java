package com.poppang.be.domain.favorite.application;

import com.poppang.be.domain.favorite.dto.request.UserFavoriteDeleteRequestDto;
import com.poppang.be.domain.favorite.dto.request.UserFavoriteRegisterRequestDto;
import com.poppang.be.domain.favorite.dto.response.FavoriteCountResponseDto;
import com.poppang.be.domain.favorite.dto.response.UserFavoritePopupResponseDto;
import com.poppang.be.domain.favorite.entity.UserFavorite;
import com.poppang.be.domain.favorite.infrastructure.UserFavoriteRepository;
import com.poppang.be.domain.popup.entity.Popup;
import com.poppang.be.domain.popup.entity.PopupImage;
import com.poppang.be.domain.popup.entity.PopupRecommend;
import com.poppang.be.domain.popup.infrastructure.PopupImageRepository;
import com.poppang.be.domain.popup.infrastructure.PopupRecommendRepository;
import com.poppang.be.domain.popup.infrastructure.PopupRepository;
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

    public List<UserFavoritePopupResponseDto> getFavoritePopupList(String userUuid) {
        Users user = usersRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        List<UserFavorite> userFavoriteList = userFavoriteRepository.findAllByUserUuid(userUuid);

        List<Long> popupIdList = userFavoriteList.stream()
                .map(userFavorite -> userFavorite.getPopup().getId())
                .toList();

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

        List<UserFavoritePopupResponseDto> userFavoritePopupResponseDtoList = new ArrayList<>();
        for (UserFavorite userFavorite : userFavoriteList) {
            Popup popup = userFavorite.getPopup();
            List<String> imageUrlList = imageMap.getOrDefault(popup.getId(), List.of());
            String recommend = recommendMap.getOrDefault(popup.getId(), null);

            userFavoritePopupResponseDtoList.add(UserFavoritePopupResponseDto.builder()
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

        return userFavoritePopupResponseDtoList;
    }

}
