package com.poppang.be.domain.keyword.dto.request;

import lombok.Getter;

@Getter
public class UserAlertKeywordDeleteDto {

    private Long userId;
    private String deleteAlertKeyword;

}
