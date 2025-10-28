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

    // 네이티브 결과를 가볍게 받기 위한 Projection
    interface RegionDistrictsRaw {
        String getRegion();     // SELECT 별칭: region
        String getDistricts();  // SELECT 별칭: districts (JSON 문자열)
    }

    @Query(value = """
        SELECT
            region,
            CONCAT(
                '[',
                GROUP_CONCAT(
                    DISTINCT CONCAT('"', district, '"')
                    ORDER BY
                        CASE WHEN district = '전체' THEN 0 ELSE 1 END,
                        district
                    SEPARATOR ','
                ),
                ']'
            ) AS districts
        FROM (
            SELECT '전체' AS region, '전체' AS district

            UNION ALL
            SELECT '서울' AS region, '전체' AS district
            UNION ALL
            SELECT
                '서울' AS region,
                CONCAT(
                    SUBSTRING_INDEX(SUBSTRING_INDEX(road_address, '구', 1), ' ', -1),
                    '구'
                ) AS district
            FROM popup
            WHERE road_address LIKE '%구%'
              AND SUBSTRING_INDEX(road_address, ' ', 1) = '서울'

            UNION ALL
            SELECT DISTINCT
                SUBSTRING_INDEX(road_address, ' ', 1) AS region,
                '전체' AS district
            FROM popup
            WHERE road_address LIKE '%구%'
              AND SUBSTRING_INDEX(road_address, ' ', 1) <> '서울'
        ) t
        WHERE district <> '구'
        GROUP BY region
        ORDER BY
            CASE WHEN region = '전체' THEN 0 ELSE 1 END,
            region
        """, nativeQuery = true)
    List<RegionDistrictsRaw> findRegionDistrictsJson();

}
