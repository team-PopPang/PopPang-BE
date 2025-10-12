package com.poppang.be.domain.favorite.presentation;

import com.poppang.be.domain.favorite.application.UserFavoriteService;
import com.poppang.be.domain.favorite.dto.request.UserFavoriteDeleteRequestDto;
import com.poppang.be.domain.favorite.dto.request.UserFavoriteRegisterRequestDto;
import com.poppang.be.domain.favorite.dto.response.FavoriteCountResponseDto;
import com.poppang.be.domain.favorite.dto.response.UserFavoritePopupResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorite")
@RequiredArgsConstructor
public class UserFavoriteController {

    private final UserFavoriteService userFavoriteService;

    @Operation(
            summary = "찜 등록",
            description = "유저가 특정 팝업을 찜합니다.",
            tags = {"[FAVORITE] 공통"}
    )
    @PostMapping
    public ResponseEntity<Void> registerFavorite(@RequestBody UserFavoriteRegisterRequestDto userFavoriteRegisterRequestDto) {
        userFavoriteService.registerFavorite(userFavoriteRegisterRequestDto);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "찜 삭제",
            description = "유저가 찜한 팝업을 취소합니다.",
            tags = {"[FAVORITE] 공통"}
    )
    @DeleteMapping
    public ResponseEntity<Void> deleteFavorite(@RequestBody UserFavoriteDeleteRequestDto userFavoriteDeleteRequestDto) {
        userFavoriteService.deleteFavorite(userFavoriteDeleteRequestDto);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "팝업별 찜 수 조회",
            description = "특정 팝업이 받은 전체 찜 개수를 조회합니다.",
            tags = {"[FAVORITE] 공통"}
    )
    @GetMapping("/count/{popupId}")
    public ResponseEntity<FavoriteCountResponseDto> getFavoriteCount(@PathVariable("popupId") Long popupId) {
        FavoriteCountResponseDto favoriteCountResponseDto = userFavoriteService.getFavoriteCount(popupId);

        return ResponseEntity.ok(favoriteCountResponseDto);
    }


    @Operation(
            summary = "유저가 찜한 팝업 목록 조회",
            description = "특정 유저가 찜한 팝업 리스트를 조회합니다.",
            tags = {"[FAVORITE] 공통"}
    )
    @GetMapping("/popup/{userId}")
    public ResponseEntity<List<UserFavoritePopupResponseDto>> getFavoritePopup(@PathVariable("userId") Long userId) {
        List<UserFavoritePopupResponseDto> userFavoritePopupResponseDtoList = userFavoriteService.getFavoritePopup(userId);

        return ResponseEntity.ok(userFavoritePopupResponseDtoList);
    }

}
