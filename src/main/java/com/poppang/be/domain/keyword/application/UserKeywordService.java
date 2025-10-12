package com.poppang.be.domain.keyword.application;

import com.poppang.be.domain.keyword.dto.request.UserKeywordDeleteDto;
import com.poppang.be.domain.keyword.dto.request.UserKeywordRegisterRequestDto;
import com.poppang.be.domain.keyword.dto.response.UserKeywordResponseDto;
import com.poppang.be.domain.keyword.entity.UserKeyword;
import com.poppang.be.domain.keyword.infrastructure.UserKeywordRepository;
import com.poppang.be.domain.users.entity.Users;
import com.poppang.be.domain.users.infrastructure.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserKeywordService {

    private final UserKeywordRepository userKeywordRepository;
    private final UsersRepository usersRepository;

    @Transactional(readOnly = true)
    public List<UserKeywordResponseDto> getUserKeywords(Long userId) {
        List<UserKeyword> userKeywordList = userKeywordRepository.findAllByUserId(userId);
        List<UserKeywordResponseDto> userKeywordResponseDtoList = new ArrayList<>();

        for (UserKeyword userKeyword : userKeywordList) {
            userKeywordResponseDtoList.add(UserKeywordResponseDto.from(userKeyword));
        }

        return userKeywordResponseDtoList;
    }

    @Transactional
    public void registerKeyword(UserKeywordRegisterRequestDto userKeywordRegisterRequestDto) {
        Users user = usersRepository.findById(userKeywordRegisterRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. "));

        UserKeyword userKeyword = UserKeyword.from(user, userKeywordRegisterRequestDto.getNewKeyword());

        userKeywordRepository.save(userKeyword);
    }

    @Transactional
    public void deleteKeyword(UserKeywordDeleteDto userKeywordDeleteDto) {
        Users user = usersRepository.findById(userKeywordDeleteDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. "));

        UserKeyword userKeyword = userKeywordRepository.findByUserIdAndKeyword(userKeywordDeleteDto.getUserId(), userKeywordDeleteDto.getDeleteKeyword())
                .orElseThrow(() -> new IllegalArgumentException("해당 키워드가 존재하지 않습니다. "));

        userKeywordRepository.delete(userKeyword);
    }

}
