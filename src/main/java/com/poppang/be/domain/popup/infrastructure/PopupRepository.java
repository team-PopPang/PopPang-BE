package com.poppang.be.domain.popup.infrastructure;

import com.poppang.be.domain.popup.entity.Popup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PopupRepository extends JpaRepository<Popup, Long> {

    Optional<Popup> findByUuid(String popupUuid);

}
