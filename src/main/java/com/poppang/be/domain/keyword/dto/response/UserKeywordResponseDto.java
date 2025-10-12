package com.poppang.be.domain.keyword.dto.response;

import com.poppang.be.domain.keyword.entity.UserKeyword;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserKeywordResponseDto {

    private String keyword;

    @Builder
    public UserKeywordResponseDto(String keyword) {
        this.keyword = keyword;
    }

    public static UserKeywordResponseDto from(UserKeyword userKeyword) {
        return UserKeywordResponseDto.builder()
                .keyword(userKeyword.getKeyword())
                .build();
    }

}
