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

    interface RegionDistrictsRaw {
        String getRegion();
        String getDistricts();
    }

    @Query("""
            select p.uuid as uuid
            from Popup p
            where p.id in :ids
            """)
    List<String> findAllUuidByIdIn(@Param("ids") List<Long> ids);

    @Query(value = """
            SELECT
                region,
                CONCAT(
                    '[',
                    GROUP_CONCAT(
                        DISTINCT CONCAT('"', district, '"')
                        ORDER BY
                            CASE
                                WHEN district = '전체' THEN 0
                                ELSE 1
                            END,
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
                CASE
                    WHEN region = '전체' THEN 0
                    ELSE 1
                END,
                region
            """, nativeQuery = true)
    List<RegionDistrictsRaw> findRegionDistrictsJson();

    @Query(value = """
            SELECT p.*, COALESCE(f.cnt, 0) AS likes
            FROM popup p
            LEFT JOIN (
                SELECT popup_id, COUNT(*) AS cnt
                FROM user_favorite
                GROUP BY popup_id
            ) f ON f.popup_id = p.id
            WHERE (:region IS NULL OR p.region = :region)
              AND (:district IS NULL OR p.road_address LIKE CONCAT('%', :district, '%'))
            ORDER BY likes DESC, p.created_at DESC
            """, nativeQuery = true)
    List<Popup> findPopupListByRegionAndLikes(@Param("region") String region,
                                              @Param("district") String district);

    @Query(value = """
            SELECT
                p.*,
                (6371 * ACOS(
                    COS(RADIANS(:latitude)) * COS(RADIANS(p.latitude))
                    * COS(RADIANS(p.longitude) - RADIANS(:longitude))
                    + SIN(RADIANS(:latitude)) * SIN(RADIANS(p.latitude))
                )) AS distance
            FROM popup p
            WHERE (:region IS NULL OR p.region = :region)
              AND (:district IS NULL OR p.road_address LIKE CONCAT('%', :district, '%'))
            ORDER BY distance ASC, p.created_at DESC
            """, nativeQuery = true)
    List<Popup> findPopupListByRegionAndDistance(String region, String district, Double latitude, Double longitude);

}
