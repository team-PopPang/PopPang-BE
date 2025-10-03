package com.poppang.be.domain.users.presentation;

import com.poppang.be.domain.users.application.UsersService;
import com.poppang.be.domain.users.dto.response.NicknameDuplicateResponseDto;
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

    @GetMapping("/nickname/duplicated")
    public ResponseEntity<NicknameDuplicateResponseDto> checkNicknameDuplicated(@RequestParam String nickname) {
        NicknameDuplicateResponseDto nicknameDuplicateResponseDto = usersService.checkNicknameDuplicated(nickname);

        return ResponseEntity.ok(nicknameDuplicateResponseDto);
    }

}
