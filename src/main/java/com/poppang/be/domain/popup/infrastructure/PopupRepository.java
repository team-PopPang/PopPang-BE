package com.poppang.be.domain.popup.infrastructure;

import com.poppang.be.domain.popup.entity.Popup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PopupRepository extends JpaRepository<Popup, Long> {

    Optional<Popup> findByUuid(String popupUuid);

    @Query("""
            select p
            from Popup p
            where p.activated = true
            and(
                lower(p.name) like lower(concat('%', :q, '%')) 
                or lower(p.captionSummary) like lower(concat('%', :q, '%') )
            )
            """)
    List<Popup> searchActivatedByKeyword(@Param("q") String q);

    List<Popup> findByActivatedTrueAndStartDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("""
            select p
            from Popup p
            where p.activated = true
            and p.startDate <= CURRENT DATE 
            and p.endDate >= CURRENT DATE 
            order by p.startDate asc 
             """)
    List<Popup> findInProgressPopupList();

}
