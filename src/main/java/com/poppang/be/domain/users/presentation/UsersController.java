package com.poppang.be.domain.users.presentation;

import com.poppang.be.domain.users.application.UsersService;
import com.poppang.be.domain.users.dto.request.ChangeNicknameRequestDto;
import com.poppang.be.domain.users.dto.response.NicknameDuplicateResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[USER] 공통", description = "유저 관련 API")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @Operation(
            summary = "닉네임 중복 검사",
            description = "입력된 닉네임이 이미 존재하는지 여부를 반환합니다."
    )
    @GetMapping("/nickname/duplicated")
    public ResponseEntity<NicknameDuplicateResponseDto> checkNicknameDuplicated(@RequestParam String nickname) {
        NicknameDuplicateResponseDto nicknameDuplicateResponseDto = usersService.checkNicknameDuplicated(nickname);

        return ResponseEntity.ok(nicknameDuplicateResponseDto);
    }

    @Operation(
            summary = "닉네임 변경",
            description = "사용자의 닉네임을 변경합니다."
    )
    @PatchMapping("/{userUuid}")
    public ResponseEntity<Void> changeNickname(
            @PathVariable String userUuid,
            @RequestBody ChangeNicknameRequestDto changeNicknameRequestDto) {

        usersService.changeNickname(userUuid, changeNicknameRequestDto);

        return ResponseEntity.ok().build();
    }

}
