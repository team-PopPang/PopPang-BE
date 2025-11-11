package com.poppang.be.domain.popup.presentation;

import com.poppang.be.domain.popup.application.PopupService;
import com.poppang.be.domain.popup.dto.request.PopupRegisterRequestDto;
import com.poppang.be.domain.popup.dto.response.PopupResponseDto;
import com.poppang.be.domain.popup.dto.response.RegionDistrictsResponse;
import com.poppang.be.domain.popup.enums.HomeSortStandard;
import com.poppang.be.domain.popup.enums.SortStandard;
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
            description = "모든 팝업스토어 정보를 조회합니다. (비활성화된 팝업 포함)"
    )
    @GetMapping
    public ResponseEntity<List<PopupResponseDto>> getAllPopupList() {
        List<PopupResponseDto> allPopupList = popupService.getAllPopupList();

        return ResponseEntity.ok(allPopupList);
    }

    @Operation(
            summary = "팝업 단건 조회",
            description = "popupUuid로 단건 팝업 조회합니다. "
    )
    @GetMapping("/{popupUuid}")
    public ResponseEntity<PopupResponseDto> getPopupByUuid(
            @PathVariable String popupUuid
    ) {
        PopupResponseDto popupResponseDto = popupService.getPopupByUuid(popupUuid);

        return ResponseEntity.ok(popupResponseDto);
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
            summary = "진행 중인 팝업 조회",
            description = "현재 날짜 기준으로 오픈 중(진행 중)인 모든 팝업스토어 정보를 조회합니다. " +
                    "시작일(`start_date`)이 오늘 이전이거나 같고, 종료일(`end_date`)이 오늘 이후이거나 같은 팝업만 반환됩니다.",
            tags = {"[POPUP] 공통"}
    )
    @GetMapping("/inProgress")
    public ResponseEntity<List<PopupResponseDto>> getInProgressPopupList() {
        List<PopupResponseDto> inProgressPopupList = popupService.getInProgressPopupList();

        return ResponseEntity.ok(inProgressPopupList);
    }

    @Tag(name = "[CRON]", description = "CRON 관련 API")
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

    @Operation(
            summary = "지역/구 목록 조회",
            description = "DB의 popup.road_address를 분석하여 지역별 구 목록을 반환합니다. "
                    + "서울은 '전체'와 실제 'OO구'들을 포함하고, 서울 외 지역은 '전체'만 포함합니다."
    )
    @GetMapping("/regions/districts")
    public ResponseEntity<List<RegionDistrictsResponse>> getRegionDistricts() {
        List<RegionDistrictsResponse> regionDistrictsResponseList = popupService.getRegionDistricts();

        return ResponseEntity.ok(regionDistrictsResponseList);
    }

    @Operation(
            summary = "팝업 필터 조회 API",
            description = """
                    지역(region), 구(district), 정렬 기준(sortStandard), 좌표(latitude, longitude)에 따라 팝업 리스트를 필터링합니다.
                    - sortStandard: LIKES(좋아요 순), DISTANCE(가까운 순)
                    - district는 '전체'로 요청하면 전체 지역을 의미합니다.
                    - latitude, longitude는 가까운순 정렬 시 필수값입니다.
                    """,
            deprecated = true
    )
    @GetMapping("/filtered")
    public List<PopupResponseDto> getFilteredPopupList(
            @RequestParam String region,
            @RequestParam(required = false) String district,
            @RequestParam(defaultValue = "LIKES") SortStandard sortStandard,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude
    ) {
        List<PopupResponseDto> filteredPopupList = popupService.getFilteredPopupList(region, district, sortStandard, latitude, longitude);

        return filteredPopupList;
    }

    @Operation(
            summary = "홈 화면용 팝업 필터 조회",
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
    public List<PopupResponseDto> getFilteredHomePopupList(
            @RequestParam String region,
            @RequestParam String district,
            @RequestParam HomeSortStandard homeSortStandard
    ) {
        List<PopupResponseDto> filteredHomePopupList = popupService.getFilteredHomePopupList(region, district, homeSortStandard);

        return filteredHomePopupList;
    }

}
