package com.poppang.be.domain.popup.infrastructure;

import com.poppang.be.domain.popup.entity.PopupSubmission;
import com.poppang.be.domain.popup.entity.PopupSubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PopupSubmissionRepository extends JpaRepository<PopupSubmission, Long> {

    List<PopupSubmission> findByStatus(PopupSubmissionStatus status);
}
