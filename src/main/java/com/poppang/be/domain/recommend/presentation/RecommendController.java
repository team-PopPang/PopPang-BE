package com.poppang.be.domain.recommend.presentation;

import com.poppang.be.domain.recommend.application.RecommendService;
import com.poppang.be.domain.recommend.dto.response.RecommendResponseDto;
import com.poppang.be.domain.recommend.entity.Recommend;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @Operation(
            summary = "추천(Recommend) 전체 조회",
            description = "전체 추천 카테고리 목록을 조회합니다.",
            tags = {"[RECOMMEND] 공통"}
    )
    @GetMapping
    public List<RecommendResponseDto> getAllRecommendList() {
        List<RecommendResponseDto> recommendResponseDtoList = recommendService.getAllRecommendList();

        return recommendResponseDtoList;
    }

}
