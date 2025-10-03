package com.poppang.be.domain.keyword.entity;

import com.poppang.be.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_keyword")
public class UserKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private Users user;

    @Column(name = "keyword", nullable = false, length = 100)
    private String keyword;

    public UserKeyword(Users user,
                       String keyword) {
        this.user = user;
        this.keyword = keyword;
    }

}
