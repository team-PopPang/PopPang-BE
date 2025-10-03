package com.poppang.be.domain.recommend.infrastructure;

import com.poppang.be.domain.recommend.entity.UserRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRecommendRepository extends JpaRepository<UserRecommend, Long> {
}
