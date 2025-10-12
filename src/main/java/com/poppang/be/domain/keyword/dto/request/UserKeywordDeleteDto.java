package com.poppang.be.domain.keyword.dto.request;

import lombok.Getter;

@Getter
public class UserKeywordDeleteDto {

    private Long userId;
    private String deleteKeyword;

}
