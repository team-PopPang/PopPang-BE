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
public class LoginResponseDto {

    private String uid;
    private String uuid;
    private Provider provider;
    private String email;
    private String nickname;
    private Role role;

    @JsonProperty("isAlerted")
    private boolean alerted;
    private String fcmToken;

    @Builder
    public LoginResponseDto(String uid,
                            String uuid,
                            Provider provider,
                            String email,
                            String nickname,
                            Role role,
                            boolean alerted,
                            String fcmToken) {
        this.uid = uid;
        this.uuid = uuid;
        this.provider = provider;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.alerted = alerted;
        this.fcmToken = fcmToken;
    }

    public static LoginResponseDto from(Users user) {
        return LoginResponseDto.builder()
                .uid(user.getUid())
                .uuid(user.getUuid())
                .provider(user.getProvider())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .alerted(user.isAlerted())
                .fcmToken(user.getFcmToken())
                .build();
    }

}
