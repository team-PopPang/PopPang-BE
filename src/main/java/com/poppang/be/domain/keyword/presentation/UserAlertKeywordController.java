package com.poppang.be.domain.keyword.presentation;

import com.poppang.be.domain.keyword.application.UserAlertKeywordService;
import com.poppang.be.domain.keyword.dto.request.UserAlertKeywordDeleteDto;
import com.poppang.be.domain.keyword.dto.request.UserAlertKeywordRegisterRequestDto;
import com.poppang.be.domain.keyword.dto.response.UserAlertKeywordResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alert-keyword")
@RequiredArgsConstructor
public class UserAlertKeywordController {

    private final UserAlertKeywordService userAlertKeywordService;

    @Operation(
            summary = "유저 알림 키워드 전체 조회",
            description = "userId를 기준으로 해당 유저가 등록한 알림 키워드 전체를 조회합니다.",
            tags = {"[USER] 알림 키워드 관리"}
    )
    @GetMapping
    public ResponseEntity<List<UserAlertKeywordResponseDto>> getUserAlertKeywords(@RequestParam("userId") Long userId) {
        List<UserAlertKeywordResponseDto> userAlertKeywordResponseDtoList = userAlertKeywordService.getUserAlertKeywords(userId);

        return ResponseEntity.ok(userAlertKeywordResponseDtoList);
    }

    @Operation(
            summary = "알림 키워드 등록",
            description = "유저 ID와 새로운 키워드를 전달하면 키워드를 등록합니다.",
            tags = {"[USER] 알림 키워드 관리"}
    )
    @PostMapping
    public ResponseEntity<Void> registerAlertKeyword(@RequestBody UserAlertKeywordRegisterRequestDto userAlertKeywordRegisterRequestDto) {
        userAlertKeywordService.registerAlertKeyword(userAlertKeywordRegisterRequestDto);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "알림 키워드 삭제",
            description = "userId와 keyword를 전달하면 해당 유저의 키워드를 삭제합니다.",
            tags = {"[USER] 알림 키워드 관리"}
    )
    @DeleteMapping
    public ResponseEntity<Void> deleteAlertKeyword(@RequestBody UserAlertKeywordDeleteDto userAlertKeywordDeleteDto) {
        userAlertKeywordService.deleteAlertKeyword(userAlertKeywordDeleteDto);

        return ResponseEntity.ok().build();
    }

}
