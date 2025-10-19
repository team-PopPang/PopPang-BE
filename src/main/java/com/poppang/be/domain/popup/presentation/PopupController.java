package com.poppang.be.domain.popup.presentation;

import com.poppang.be.domain.popup.application.PopupService;
import com.poppang.be.domain.popup.dto.request.PopupRegisterRequestDto;
import com.poppang.be.domain.popup.dto.response.PopupResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/popup")
@RequiredArgsConstructor
public class PopupController {

    private final PopupService popupService;

    @Operation(
            summary = "팝업 전체 조회",
            description = "현재 활성화된 모든 팝업스토어 정보를 조회합니다.",
            tags = {"[POPUP] 공통"}
    )
    @GetMapping
    public List<PopupResponseDto> getAllPopupList() {
        List<PopupResponseDto> allPopupList = popupService.getAllPopupList();

        return allPopupList;
    }

    @PostMapping
    public ResponseEntity<Void> registerPopup(@RequestBody PopupRegisterRequestDto popupRegisterRequestDto) {
        popupService.registerPopup(popupRegisterRequestDto);

        return ResponseEntity.ok().build();
    }

}
