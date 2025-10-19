package com.poppang.be.domain.popup.infrastructure;

import com.poppang.be.domain.popup.entity.PopupRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PopupRecommendRepository extends JpaRepository<PopupRecommend, Long> {

}
