package com.poppang.be.domain.popup.presentation;

import com.poppang.be.domain.popup.application.PopupUserService;
import com.poppang.be.domain.popup.dto.response.PopupResponseDto;
import com.poppang.be.domain.popup.dto.response.PopupUserResponseDto;
import com.poppang.be.domain.popup.enums.HomeSortStandard;
import com.poppang.be.domain.popup.enums.MapSortStandard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[POPUP-USER] 개인화", description = "유저별 팝업 조회 API")
@RestController
@RequestMapping("/api/v1/users/{userUuid}/popups")
@RequiredArgsConstructor
public class PopupUserController {

    private final PopupUserService popupUserService;

    @Operation(
            summary = "팝업 전체 조회",
            description = "모든 팝업스토어 정보를 조회합니다. (비활성화된 팝업 포함)"
    )
    @GetMapping
    public ResponseEntity<List<PopupUserResponseDto>> getAllPopupList(
            @PathVariable String userUuid
    ) {
        List<PopupUserResponseDto> popupUserResponseDtoList = popupUserService.getAllPopupList(userUuid);

        return ResponseEntity.ok(popupUserResponseDtoList);
    }

    @Operation(
            summary = "팝업 단건 조회",
            description = "popupUuid로 단건 팝업 조회합니다. "
    )
    @GetMapping("/{popupUuid}")
    public ResponseEntity<PopupUserResponseDto> getPopupByUuid(
            @PathVariable String userUuid,
            @PathVariable String popupUuid
    ) {
        PopupUserResponseDto popupUserResponseDto = popupUserService.getPopupByUuid(userUuid, popupUuid);

        return ResponseEntity.ok(popupUserResponseDto);
    }

    @Operation(
            summary = "다가오는 팝업 조회 (D-1 ~ D-10)",
            description = "오늘부터 10일 이내에 시작하는 팝업을 반환합니다."
    )
    @GetMapping("/upcoming")
    public ResponseEntity<List<PopupUserResponseDto>> getUpcomingPopupList(
            @PathVariable String userUuid,
            @Parameter(description = "며칠 뒤까지 조회 (기본 10)") @RequestParam(name = "upcomingDays", required = false) Integer upcomingDays
    ) {
        List<PopupUserResponseDto> upcomingPopupUserList = popupUserService.getUpcomingPopupList(userUuid, upcomingDays);

        return ResponseEntity.ok(upcomingPopupUserList);
    }

    @Operation(
            summary = "팝업 검색",
            description = "특정 키워드로 팝업스토어를 검색합니다."
    )
    @GetMapping("/search")
    public ResponseEntity<List<PopupUserResponseDto>> getSearchPopupList(
            @PathVariable String userUuid,
            @RequestParam("q") String q) {
        List<PopupUserResponseDto> searchPopupUserList = popupUserService.getSearchPopupList(userUuid, q);

        return ResponseEntity.ok(searchPopupUserList);
    }

    @Operation(
            summary = "진행 중인 팝업 조회",
            description = "현재 날짜 기준으로 오픈 중(진행 중)인 모든 팝업스토어 정보를 조회합니다. " +
                    "시작일(`start_date`)이 오늘 이전이거나 같고, 종료일(`end_date`)이 오늘 이후이거나 같은 팝업만 반환됩니다.",
            tags = {"[POPUP] 공통"}
    )
    @GetMapping("/inProgress")
    public ResponseEntity<List<PopupUserResponseDto>> getInProgressPopupList(
            @PathVariable String userUuid
    ) {
        List<PopupUserResponseDto> inProgressPopupList = popupUserService.getInProgressPopupList(userUuid);

        return ResponseEntity.ok(inProgressPopupList);
    }

    @Operation(
            summary = "[홈 뷰] 팝업 필터 조회",
            description = """
                    홈 화면에서 지역(region), 구(district), 정렬 기준(homeSortStandard)에 따라 팝업 리스트를 조회합니다.
                    - homeSortStandard:
                      • NEWEST : 최근 오픈 순
                      • CLOSING_SOON : 곧 종료될 순
                      • MOST_FAVORITED : 좋아요 많은 순
                      • MOST_VIEWED : 조회수 많은 순
                    - region, district는 선택된 지역과 구를 의미하며,
                      district가 '전체'일 경우 해당 지역 내 모든 팝업을 조회합니다.
                    - 반환되는 리스트는 진행 중(is_active = true, 날짜 유효)인 팝업만 포함합니다.
                    """
    )
    @GetMapping("/filtered/home")
    public List<PopupUserResponseDto> getFilteredHomePopupList(
            @PathVariable String userUuid,
            @RequestParam String region,
            @RequestParam String district,
            @RequestParam HomeSortStandard homeSortStandard
    ) {
        List<PopupUserResponseDto> filteredHomePopupList = popupUserService.getFilteredHomePopupList(userUuid, region, district, homeSortStandard);

        return filteredHomePopupList;
    }

    @Operation(
            summary = "[지도뷰] 팝업 필터 조회 API",
            description = """
                지역(region), 구(district), 정렬 기준(mapSortStandard), 좌표(latitude, longitude)에 따라 팝업 리스트를 필터링합니다.
                
                • mapSortStandard:
                  - NEAREST(가까운 순)  ← 이 경우 latitude/longitude 필수
                  - MOST_FAVORITED(찜순)
                  - MOST_VIEWED(조회수순)
                  - NEWEST(최신순)
                  - CLOSING_SOON(마감임박순)
                
                • district는 '전체'로 요청하면 전체 지역을 의미합니다.
                • latitude, longitude는 가까운순 정렬 시 필수값입니다.
                """
    )
    @GetMapping("/filtered/map")
    public ResponseEntity<List<PopupUserResponseDto>> getFilteredMapPopupList(
            @PathVariable String userUuid,
            @RequestParam String region,
            @RequestParam String district,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam MapSortStandard mapSortStandard
    ) {
        List<PopupUserResponseDto> filteredMapPopupList = popupUserService.getFilteredMapPopupList(userUuid, region, district, latitude, longitude, mapSortStandard);

        return ResponseEntity.ok(filteredMapPopupList);
    }




}
