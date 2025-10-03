package com.poppang.be.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poppang.be.common.enums.Role;
import com.poppang.be.domain.users.entity.Provider;
import com.poppang.be.domain.users.entity.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupResponseDto {

    private String uid;
    private Provider provider;
    private String email;
    private String nickname;
    private Role role;

    @JsonProperty("isAlerted")
    private boolean alerted;
    private String fcmToken;

    @Builder
    public SignupResponseDto(String uid,
                             Provider provider,
                             String email,
                             String nickname,
                             Role role,
                             boolean alerted,
                             String fcmToken) {
        this.uid = uid;
        this.provider = provider;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.alerted = alerted;
        this.fcmToken = fcmToken;
    }

    public static SignupResponseDto from(Users user) {
        return SignupResponseDto.builder()
                .uid(user.getUid())
                .provider(user.getProvider())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .alerted(user.isAlerted())
                .fcmToken(user.getFcmToken())
                .build();
    }

}
