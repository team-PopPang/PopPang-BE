package com.poppang.be.domain.popup.application;

import com.poppang.be.domain.popup.dto.response.PopupResponseDto;
import com.poppang.be.domain.popup.entity.Popup;
import com.poppang.be.domain.popup.infrastructure.PopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PopupService {

    private final PopupRepository popupRepository;

    public List<PopupResponseDto> getAllPopupList() {
        List<Popup> popupList = popupRepository.findAll();
        List<PopupResponseDto> popupResponseDtoList = new ArrayList<>();

        for (Popup popup : popupList) {
            popupResponseDtoList.add(PopupResponseDto.from(popup));
        }

        return popupResponseDtoList;
    }

}
