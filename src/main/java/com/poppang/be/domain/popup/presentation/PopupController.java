package com.poppang.be.domain.popup.presentation;

import com.poppang.be.domain.popup.application.PopupService;
import com.poppang.be.domain.popup.dto.request.PopupRegisterRequestDto;
import com.poppang.be.domain.popup.dto.response.PopupResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[POPUP] 공통", description = "팝업스토어 관련 API")
@RestController
@RequestMapping("/api/v1/popup")
@RequiredArgsConstructor
public class PopupController {

    private final PopupService popupService;

    @Operation(
            summary = "팝업 전체 조회",
            description = "현재 활성화된 모든 팝업스토어 정보를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<List<PopupResponseDto>> getAllPopupList() {
        List<PopupResponseDto> allPopupList = popupService.getAllPopupList();

        return ResponseEntity.ok(allPopupList);
    }

    @Operation(
            summary = "팝업 검색",
            description = "특정 키워드로 팝업스토어를 검색합니다."
    )
    @GetMapping("/search")
    public ResponseEntity<List<PopupResponseDto>> getSearchPopupList
            (@RequestParam("q") String q) {
        List<PopupResponseDto> searchPopupList = popupService.getSearchPopupList(q);

        return ResponseEntity.ok(searchPopupList);
    }

    @Operation(
            summary = "다가오는 팝업 조회 (D-1 ~ D-10)",
            description = "오늘부터 10일 이내에 시작하는 팝업을 반환합니다.",
            tags = {"[POPUP] 공통"}
    )
    @GetMapping("/upcoming")
    public ResponseEntity<List<PopupResponseDto>> getUpcomingPopupList(
            @Parameter(description = "며칠 뒤까지 조회 (기본 10)") @RequestParam(name = "upcomingDays", required = false) Integer upcomingDays
    ) {
        List<PopupResponseDto> upcomingPopupList = popupService.getUpcomingPopupList(upcomingDays);

        return ResponseEntity.ok(upcomingPopupList);
    }

    @Operation(
            summary = "팝업 등록",
            description = "크롤링 또는 관리자가 신규 팝업스토어 데이터를 등록합니다. " +
                    "이미지 리스트(`imageList`)와 추천 ID(`recommendIds`)를 함께 전달해야 합니다."
    )
    @PostMapping
    public ResponseEntity<Void> registerPopup(@RequestBody PopupRegisterRequestDto popupRegisterRequestDto) {
        popupService.registerPopup(popupRegisterRequestDto);

        return ResponseEntity.ok().build();
    }

}
