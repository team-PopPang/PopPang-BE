package com.poppang.be.domain.users.presentation;

import com.poppang.be.domain.users.application.UsersService;
import com.poppang.be.domain.users.dto.response.NicknameDuplicateResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @Operation(
            summary = "닉네임 중복 검사",
            description = "입력된 닉네임이 이미 존재하는지 여부를 반환합니다.",
            tags = {"[USER] 공통"}
    )
    @GetMapping("/nickname/duplicated")
    public ResponseEntity<NicknameDuplicateResponseDto> checkNicknameDuplicated(@RequestParam String nickname) {
        NicknameDuplicateResponseDto nicknameDuplicateResponseDto = usersService.checkNicknameDuplicated(nickname);

        return ResponseEntity.ok(nicknameDuplicateResponseDto);
    }

}
