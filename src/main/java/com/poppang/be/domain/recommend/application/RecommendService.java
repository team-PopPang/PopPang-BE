package com.poppang.be.domain.recommend.application;

import com.poppang.be.domain.recommend.dto.response.RecommendResponseDto;
import com.poppang.be.domain.recommend.entity.Recommend;
import com.poppang.be.domain.recommend.infrastructure.RecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final RecommendRepository recommendRepository;

    public List<RecommendResponseDto> getAllRecommendList() {
        List<Recommend> recommendList = recommendRepository.findAll();
        List<RecommendResponseDto> recommendResponseDtoList = new ArrayList<>();

        for (Recommend recommend : recommendList) {
            recommendResponseDtoList.add(RecommendResponseDto.from(recommend));
        }

        return recommendResponseDtoList;
    }

}
