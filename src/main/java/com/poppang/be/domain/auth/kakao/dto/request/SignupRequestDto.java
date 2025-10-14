package com.poppang.be.domain.auth.kakao.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poppang.be.common.enums.Role;
import com.poppang.be.domain.users.entity.Provider;
import lombok.Getter;

import java.util.List;

@Getter
public class SignupRequestDto {

    private String uid;
    private Provider provider;
    private String email;
    private String nickname;
    private Role role;

    @JsonProperty("isAlerted")
    private boolean alerted;
    private String fcmToken;
    private List<String> alertKeywordList;
    private List<Long> recommendList;

}
