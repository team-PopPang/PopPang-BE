package com.poppang.be.domain.keyword.dto.request;

import lombok.Getter;

@Getter
public class UserKeywordRegisterRequestDto {

    private Long userId;
    private String newKeyword;

}
