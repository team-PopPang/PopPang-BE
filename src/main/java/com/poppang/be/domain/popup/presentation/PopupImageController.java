package com.poppang.be.domain.popup.presentation;

import com.poppang.be.domain.popup.application.PopupImageService;
import com.poppang.be.domain.popup.dto.request.PopupImageUpsertRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/popup")
@RequiredArgsConstructor
public class PopupImageController {

    private final PopupImageService popupImageService;

    @Operation(
            summary = "팝업 이미지 등록/수정 (Upsert)",
            description = "특정 팝업(UUID 기준)의 이미지를 등록 또는 수정합니다 ",
            tags = {"[POPUP] 이미지 관리"}
    )
    @PutMapping("/{popupUuid}/images")
    public ResponseEntity<Void> upsertImage(
            @PathVariable String popupUuid,
            @RequestBody List<PopupImageUpsertRequestDto> popupImageUpsertRequestDtoList
    ) {
        popupImageService.upsertImages(popupUuid, popupImageUpsertRequestDtoList);

        return ResponseEntity.ok().build();
    }

}
