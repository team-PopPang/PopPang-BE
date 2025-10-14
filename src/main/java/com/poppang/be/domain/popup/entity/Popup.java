package com.poppang.be.domain.popup.entity;

import com.poppang.be.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "popup")
public class Popup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, length = 36)
    private String uuid;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "open_time", nullable = true)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = true)
    private LocalTime closeTime;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "road_address", nullable = true, length = 255)
    private String roadAddress;

    @Column(name = "region", nullable = false, length = 100)
    private String region;

    @Column(name = "latitude", nullable = true)
    private Double latitude;

    @Column(name = "longitude", nullable = true)
    private Double longitude;

    @Column(name = "geocoding_query", nullable = true)
    private String geocodingQuery;

    @Column(name = "insta_post_id", nullable = false, length = 255, unique = true)
    private String instaPostId;

    @Column(name = "insta_post_url", nullable = false, length = 255)
    private String instaPostUrl;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "caption_summary", nullable = false, columnDefinition = "TEXT")
    private String captionSummary;

    @Column(name = "caption", nullable = false, columnDefinition = "TEXT")
    private String caption;

    @Column(name = "image_url", nullable = true, columnDefinition = "TEXT")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = true)
    private MediaType mediaType;

    @Column(name = "is_active", nullable = false)
    private boolean activated;

    @Column(name = "error_code", length = 255)
    private String errorCode;

    @PrePersist
    private void ensureUuid() {
        if (this.uuid == null || this.uuid.isBlank()) {
            this.uuid = UUID.randomUUID().toString();
        }
    }

}
