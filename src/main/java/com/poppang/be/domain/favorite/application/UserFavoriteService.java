package com.poppang.be.domain.favorite.application;

import com.poppang.be.domain.favorite.dto.request.UserFavoriteDeleteRequestDto;
import com.poppang.be.domain.favorite.dto.request.UserFavoriteRegisterRequestDto;
import com.poppang.be.domain.favorite.dto.response.FavoriteCountResponseDto;
import com.poppang.be.domain.favorite.dto.response.UserFavoritePopupResponseDto;
import com.poppang.be.domain.favorite.entity.UserFavorite;
import com.poppang.be.domain.favorite.infrastructure.UserFavoriteRepository;
import com.poppang.be.domain.popup.entity.Popup;
import com.poppang.be.domain.popup.infrastructure.PopupRepository;
import com.poppang.be.domain.users.entity.Users;
import com.poppang.be.domain.users.infrastructure.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFavoriteService {

    private final UsersRepository usersRepository;
    private final PopupRepository popupRepository;
    private final UserFavoriteRepository userFavoriteRepository;

    @Transactional
    public void registerFavorite(UserFavoriteRegisterRequestDto userFavoriteRegisterRequestDto) {
        Users user = usersRepository.findById(userFavoriteRegisterRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. "));

        Popup popup = popupRepository.findById(userFavoriteRegisterRequestDto.getPopupId())
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
        UserFavorite userFavorite = userFavoriteRepository.findByUserIdAndPopupId(userFavoriteDeleteRequestDto.getUserId(), userFavoriteDeleteRequestDto.getPopupId())
                .orElseThrow(() -> new IllegalStateException("해당 찜 기록이 없습니다. "));

        userFavoriteRepository.delete(userFavorite);
    }

    @Transactional(readOnly = true)
    public FavoriteCountResponseDto getFavoriteCount(Long popupId) {
        long count = userFavoriteRepository.countByPopupId(popupId);

        return FavoriteCountResponseDto.from(count);
    }

    public List<UserFavoritePopupResponseDto> getFavoritePopup(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        List<UserFavorite> userFavoriteList = userFavoriteRepository.findAllByUser_Id(userId);

        List<UserFavoritePopupResponseDto> popupList = new ArrayList<>();
        for (UserFavorite userFavorite : userFavoriteList) {
            popupList.add(UserFavoritePopupResponseDto.from(userFavorite.getPopup()));
        }

        return popupList;
    }

}
