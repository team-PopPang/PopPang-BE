package com.poppang.be.domain.users.application;

import com.poppang.be.domain.users.dto.request.ChangeNicknameRequestDto;
import com.poppang.be.domain.users.dto.response.NicknameDuplicateResponseDto;
import com.poppang.be.domain.users.entity.Users;
import com.poppang.be.domain.users.infrastructure.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
