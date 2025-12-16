package com.poppang.be.domain.keyword.application;

import com.poppang.be.common.exception.BaseException;
import com.poppang.be.common.exception.ErrorCode;
import com.poppang.be.domain.keyword.dto.request.UserAlertKeywordDeleteDto;
import com.poppang.be.domain.keyword.dto.request.UserAlertKeywordRegisterRequestDto;
import com.poppang.be.domain.keyword.dto.response.UserAlertKeywordResponseDto;
import com.poppang.be.domain.keyword.entity.UserAlertKeyword;
import com.poppang.be.domain.keyword.infrastructure.UserAlertKeywordRepository;
import com.poppang.be.domain.users.entity.Users;
import com.poppang.be.domain.users.infrastructure.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAlertKeywordService {

    private final UserAlertKeywordRepository userAlertKeywordRepository;
    private final UsersRepository usersRepository;

    @Transactional(readOnly = true)
    public List<UserAlertKeywordResponseDto> getUserAlertKeywordList(String userUuid) {
        List<UserAlertKeyword> userAlertKeywordList = userAlertKeywordRepository.findAllByUserUuid(userUuid);
        List<UserAlertKeywordResponseDto> userAlertKeywordResponseDtoList = new ArrayList<>();

        for (UserAlertKeyword userAlertKeyword : userAlertKeywordList) {
            userAlertKeywordResponseDtoList.add(UserAlertKeywordResponseDto.from(userAlertKeyword));
        }

        return userAlertKeywordResponseDtoList;
    }

    @Transactional
    public void registerAlertKeyword(UserAlertKeywordRegisterRequestDto userAlertKeywordRegisterRequestDto) {
        Users user = usersRepository.findByUuid(userAlertKeywordRegisterRequestDto.getUserUuid())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        UserAlertKeyword userAlertKeyword = UserAlertKeyword.from(user, userAlertKeywordRegisterRequestDto.getNewAlertKeyword());

        userAlertKeywordRepository.save(userAlertKeyword);
    }

    @Transactional
    public void deleteAlertKeyword(UserAlertKeywordDeleteDto userAlertKeywordDeleteDto) {
        Users user = usersRepository.findByUuid(userAlertKeywordDeleteDto.getUserUuid())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        UserAlertKeyword userAlertKeyword = userAlertKeywordRepository.findByUserUuidAndAlertKeyword(userAlertKeywordDeleteDto.getUserUuid(), userAlertKeywordDeleteDto.getDeleteAlertKeyword())
                .orElseThrow(() -> new BaseException(ErrorCode.ALERT_KEYWORD_NOT_FOUND));

        userAlertKeywordRepository.delete(userAlertKeyword);
    }

}
