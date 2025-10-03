package com.poppang.be.domain.users.application;

import com.poppang.be.domain.users.dto.response.NicknameDuplicateResponseDto;
import com.poppang.be.domain.users.infrastructure.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    public NicknameDuplicateResponseDto checkNicknameDuplicated(String nickname) {

        boolean duplicated = usersRepository.existsByNickname(nickname);

        return NicknameDuplicateResponseDto.from(duplicated);
    }

}
