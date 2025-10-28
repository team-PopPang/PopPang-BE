package com.poppang.be.domain.popup.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RegionDistrictsResponse {

    private String region;
    private List<String> districts;

    @Builder
    public RegionDistrictsResponse(String region,
                                   List<String> districts) {
        this.region = region;
        this.districts = districts;
    }

}
