package com.poppang.be.domain.alert.application;

import com.poppang.be.domain.alert.dto.request.UserAlertDeleteRequestDto;
import com.poppang.be.domain.alert.dto.request.UserAlertRegisterRequestDto;
import com.poppang.be.domain.alert.dto.response.UserAlertResponseDto;
import com.poppang.be.domain.alert.entity.UserAlert;
import com.poppang.be.domain.alert.infrastructure.UserAlertRepository;
import com.poppang.be.domain.favorite.infrastructure.UserFavoriteRepository;
import com.poppang.be.domain.popup.dto.response.PopupUserResponseDto;
import com.poppang.be.domain.popup.entity.Popup;
import com.poppang.be.domain.popup.infrastructure.PopupRepository;
import com.poppang.be.domain.popup.mapper.PopupUserResponseDtoMapper;
import com.poppang.be.domain.users.entity.Users;
import com.poppang.be.domain.users.infrastructure.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAlertService {

    private final UserAlertRepository userAlertRepository;
    private final UsersRepository usersRepository;
    private final PopupRepository popupRepository;
    private final UserFavoriteRepository userFavoriteRepository;
    private final PopupUserResponseDtoMapper popupUserResponseDtoMapper;

    @Transactional
    public void registerUserAlert(String userUuid, UserAlertRegisterRequestDto userAlertRegisterRequestDto) {
        Users user = usersRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. uuid=" + userUuid));

        Popup popup = popupRepository.findByUuid(userAlertRegisterRequestDto.getPopupUuid())
                .orElseThrow(() -> new IllegalArgumentException("팝업을 찾을 수 없습니다. uuid=" + userAlertRegisterRequestDto.getPopupUuid()));

        if (userAlertRepository.existsByUser_IdAndPopup_Id(user.getId(), popup.getId())) {
            throw new IllegalStateException("이미 해당 팝업에 대한 알림 기록이 존재합니다.");
        }

        UserAlert userAlert = UserAlert.builder()
                .user(user)
                .popup(popup)
                .alertedAt(LocalDateTime.now())
                .readAt(null)
                .build();

        userAlertRepository.save(userAlert);
    }

    @Transactional
    public void deleteUserAlert(String userUuid, UserAlertDeleteRequestDto userAlertDeleteRequestDto) {
        Users user = usersRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. uuid=" + userUuid));

        Popup popup = popupRepository.findByUuid(userAlertDeleteRequestDto.getPopupUuid())
                .orElseThrow(() -> new IllegalArgumentException("팝업을 찾을 수 없습니다. uuid=" + userAlertDeleteRequestDto.getPopupUuid()));

        UserAlert userAlert = userAlertRepository
                .findByUser_IdAndPopup_Id(user.getId(), popup.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 유저/팝업에 대한 알림 이력이 없습니다. userId=" + user.getId() + ", popupId=" + popup.getId()
                ));

        userAlertRepository.delete(userAlert);
    }

    @Transactional(readOnly = true)
    public List<UserAlertResponseDto> getUserAlertPopupList(String userUuid) {
        Users user = usersRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. uuid=" + userUuid));

        List<UserAlert> userAlertList = userAlertRepository.findAllByUser_IdOrderByAlertedAtDesc(user.getId());

        if (userAlertList.isEmpty()) {
            return List.of();
        }

        List<Popup> popupList = userAlertList
                .stream()
                .map(UserAlert::getPopup)
                .toList();

        Set<Long> favoritedPopupIdList = userFavoriteRepository.findAllByUserUuid(userUuid)
                .stream()
                .map(f -> f.getPopup().getId())
                .collect(Collectors.toSet());

        List<PopupUserResponseDto> popupUserResponseDtoList =
                popupUserResponseDtoMapper.toPopupUserResponseDtoList(popupList, favoritedPopupIdList);

        List<UserAlertResponseDto> result = new ArrayList<>(popupUserResponseDtoList.size());

        for (int i = 0; i < popupUserResponseDtoList.size(); i++) {
            PopupUserResponseDto popupDto = popupUserResponseDtoList.get(i);
            UserAlert userAlert = userAlertList.get(i);  // 같은 순서라고 가정

            boolean isRead = (userAlert.getReadAt() != null);

            UserAlertResponseDto alertDto = UserAlertResponseDto.from(popupDto, isRead);
            result.add(alertDto);
        }

        return result;
    }

    @Transactional
    public void readUserAlertPopup(String userUuid, String popupUuid) {
        Users user = usersRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. uuid=" + userUuid));

        Popup popup = popupRepository.findByUuid(popupUuid)
                .orElseThrow(() -> new IllegalArgumentException("팝업을 찾을 수 없습니다. uuid=" + popupUuid));

        UserAlert userAlert = userAlertRepository.findByUser_IdAndPopup_Id(user.getId(), popup.getId())
                .orElseThrow(() -> new IllegalArgumentException("알림 팝업을 찾을 수 없습니다. "));

        if (userAlert.getReadAt() == null) {
            userAlert.markAsRead();
        }
    }

}
