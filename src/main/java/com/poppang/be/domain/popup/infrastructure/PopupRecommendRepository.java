package com.poppang.be.domain.popup.infrastructure;

import com.poppang.be.domain.popup.entity.PopupRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PopupRecommendRepository extends JpaRepository<PopupRecommend, Long> {

    List<PopupRecommend> findAllByPopup_IdIn(List<Long> popupIdList);

}
