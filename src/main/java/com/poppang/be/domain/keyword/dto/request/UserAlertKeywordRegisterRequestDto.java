package com.poppang.be.domain.keyword.dto.request;

import lombok.Getter;

@Getter
public class UserAlertKeywordRegisterRequestDto {

    private Long userId;
    private String newAlertKeyword;

}
