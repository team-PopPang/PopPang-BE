package com.poppang.be.domain.recommend.dto.response;

import com.poppang.be.domain.recommend.entity.Recommend;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecommendResponseDto {

    private String uuid;
    private String recommendName;

    @Builder
    public RecommendResponseDto(String uuid,
                                String recommendName) {
        this.uuid = uuid;
        this.recommendName = recommendName;

    }

    public static RecommendResponseDto from(Recommend recommend) {
        return RecommendResponseDto.builder()
                .uuid(recommend.getUuid())
                .recommendName(recommend.getRecommendName())
                .build();
    }

}
