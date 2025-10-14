package com.poppang.be.domain.keyword.application;

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
    public List<UserAlertKeywordResponseDto> getUserAlertKeywords(Long userId) {
        List<UserAlertKeyword> userAlertKeywordList = userAlertKeywordRepository.findAllByUserId(userId);
        List<UserAlertKeywordResponseDto> userAlertKeywordResponseDtoList = new ArrayList<>();

        for (UserAlertKeyword userAlertKeyword : userAlertKeywordList) {
            userAlertKeywordResponseDtoList.add(UserAlertKeywordResponseDto.from(userAlertKeyword));
        }

        return userAlertKeywordResponseDtoList;
    }

    @Transactional
    public void registerAlertKeyword(UserAlertKeywordRegisterRequestDto userAlertKeywordRegisterRequestDto) {
        Users user = usersRepository.findById(userAlertKeywordRegisterRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. "));

        UserAlertKeyword userAlertKeyword = UserAlertKeyword.from(user, userAlertKeywordRegisterRequestDto.getNewAlertKeyword());

        userAlertKeywordRepository.save(userAlertKeyword);
    }

    @Transactional
    public void deleteAlertKeyword(UserAlertKeywordDeleteDto userAlertKeywordDeleteDto) {
        Users user = usersRepository.findById(userAlertKeywordDeleteDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. "));

        UserAlertKeyword userAlertKeyword = userAlertKeywordRepository.findByUserIdAndAlertKeyword(userAlertKeywordDeleteDto.getUserId(), userAlertKeywordDeleteDto.getDeleteAlertKeyword())
                .orElseThrow(() -> new IllegalArgumentException("해당 키워드가 존재하지 않습니다. "));

        userAlertKeywordRepository.delete(userAlertKeyword);
    }

}
