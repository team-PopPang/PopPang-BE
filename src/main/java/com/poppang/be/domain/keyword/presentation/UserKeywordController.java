package com.poppang.be.domain.keyword.presentation;

import com.poppang.be.domain.keyword.application.UserKeywordService;
import com.poppang.be.domain.keyword.dto.request.UserKeywordDeleteDto;
import com.poppang.be.domain.keyword.dto.request.UserKeywordRegisterRequestDto;
import com.poppang.be.domain.keyword.dto.response.UserKeywordResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/keyword")
@RequiredArgsConstructor
public class UserKeywordController {

    private final UserKeywordService userKeywordService;

    @Operation(
            summary = "유저 키워드 전체 조회",
            description = "userId를 기준으로 해당 유저가 등록한 키워드 전체를 조회합니다.",
            tags = {"[USER] 키워드 관리"}
    )
    @GetMapping
    public ResponseEntity<List<UserKeywordResponseDto>> getUserKeywords(@RequestParam("userId") Long userId) {
        List<UserKeywordResponseDto> userKeywordResponseDtoList = userKeywordService.getUserKeywords(userId);

        return ResponseEntity.ok(userKeywordResponseDtoList);
    }

    @Operation(
            summary = "키워드 등록",
            description = "유저 ID와 새로운 키워드를 전달하면 키워드를 등록합니다.",
            tags = {"[USER] 키워드 관리"}
    )
    @PostMapping
    public ResponseEntity<Void> registerKeyword(@RequestBody UserKeywordRegisterRequestDto userKeywordRegisterRequestDto) {
        userKeywordService.registerKeyword(userKeywordRegisterRequestDto);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "키워드 삭제",
            description = "userId와 keyword를 전달하면 해당 유저의 키워드를 삭제합니다.",
            tags = {"[USER] 키워드 관리"}
    )
    @DeleteMapping
    public ResponseEntity<Void> deleteKeyword(@RequestBody UserKeywordDeleteDto userKeywordDeleteDto) {
        userKeywordService.deleteKeyword(userKeywordDeleteDto);

        return ResponseEntity.ok().build();
    }

}
