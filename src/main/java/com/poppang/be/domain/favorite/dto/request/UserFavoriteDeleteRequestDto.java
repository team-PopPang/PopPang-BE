package com.poppang.be.domain.favorite.dto.request;

import lombok.Getter;

@Getter
public class UserFavoriteDeleteRequestDto {

    private Long userId;
    private Long popupId;

}
