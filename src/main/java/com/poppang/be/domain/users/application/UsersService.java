package com.poppang.be.domain.users.application;

import com.poppang.be.domain.popup.dto.response.UserUpdateFcmTokenResquestDto;
import com.poppang.be.domain.users.dto.request.ChangeNicknameRequestDto;
import com.poppang.be.domain.users.dto.request.UpdateAlertStatusRequestDto;
import com.poppang.be.domain.users.dto.response.*;
import com.poppang.be.domain.users.entity.Users;
import com.poppang.be.domain.users.infrastructure.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    public NicknameDuplicateResponseDto checkNicknameDuplicated(String nickname) {

        boolean duplicated = usersRepository.existsByNickname(nickname);

        return NicknameDuplicateResponseDto.from(duplicated);
    }

    @Transactional
    public void changeNickname(String userUuid, ChangeNicknameRequestDto changeNicknameRequestDto) {

        String rawNickname = changeNicknameRequestDto.getNickname();
        String nickname = rawNickname.trim();

        Users user = usersRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        boolean duplicated = usersRepository.existsByNickname(changeNicknameRequestDto.getNickname());
        if (duplicated) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다. ");
        }

        user.changeNickname(changeNicknameRequestDto);
    }

    @Transactional
    public void softDeleteUser(String userUuid) {

        Users user = usersRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        user.softDelete();
    }

    @Transactional
    public void restoreUser(String userUuid) {

        Users user = usersRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        user.restore();
    }

    @Transactional(readOnly = true)
    public boolean isFcmTokenDuplicated(String userUuid, String fcmToken) {
        Users user = usersRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. "));

        String savedToken = user.getFcmToken();

        if (savedToken == null) {
            return false;
        }
        boolean duplicated = savedToken.equals(fcmToken);

        return duplicated;
    }

    @Transactional
    public void updateFcmToken(String userUuid, UserUpdateFcmTokenResquestDto userUpdateFcmTokenResquestDto) {
        Users user = usersRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. "));

        user.updateFcmToken(userUpdateFcmTokenResquestDto.getFcmToken());
    }

    @Transactional(readOnly = true)
    public List<UserWithKeywordListResponseDto> getUserWithKeywordList() {
        List<UserWithKeywordListResponseDto> userWithKeywordListResponseDtoList = usersRepository.findUserWithAlertKeywordList()
                .stream()
                .map(r -> UserWithKeywordListResponseDto.builder()
                        .userId(r.getUserId())
                        .nickname(r.getNickname())
                        .fcmToken(r.getFcmToken())
                        .keyword(r.getKeyword())
                        .build())
                .toList();

        return userWithKeywordListResponseDtoList;
    }

    public List<UserWithKeywordListResponseDtoB> getUserWithKeywordListB() {
        List<UserWithKeywordListResponseDtoB> userWithKeywordListResponseDtoBList = usersRepository.findUserWithAlertKeywordListB()
                .stream()
                .map(r -> UserWithKeywordListResponseDtoB.builder()
                        .userId(r.getUserId())
                        .nickname(r.getNickname())
                        .fcmToken(r.getFcmToken())
                        .keywordList(
                                (r.getKeywordList() == null || r.getKeywordList().isBlank())
                                        ? List.of()
                                        : Arrays.stream(r.getKeywordList().split(","))
                                        .map(String::trim)
                                        .filter(s -> !s.isBlank())
                                        .toList()
                        )
                        .build())
                        .toList();

        return userWithKeywordListResponseDtoBList;
    }

    @Transactional(readOnly = true)
    public GetUserResponseDto getUserInfo(String userUuid) {
        Users user = usersRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. "));

        GetUserResponseDto getUserResponseDto = GetUserResponseDto.from(user);

        return getUserResponseDto;
    }

    @Transactional
    public UpdateAlertStatusResponseDto updateAlertStatus(String userUuid, UpdateAlertStatusRequestDto updateAlertStatusRequestDto) {
        Users user = usersRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. "));

        user.updateAlerted(updateAlertStatusRequestDto.isAlerted());

        UpdateAlertStatusResponseDto updateAlertStatusResponseDto = UpdateAlertStatusResponseDto.builder()
                .userUuid(userUuid)
                .alerted(user.isAlerted())
                .build();

        return updateAlertStatusResponseDto;
    }

}
