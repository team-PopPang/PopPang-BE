package com.poppang.be.domain.keyword.infrastructure;

import com.poppang.be.domain.keyword.entity.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {

    List<UserKeyword> findAllByUserId(Long userId);

    Optional<UserKeyword> findByUserIdAndKeyword(Long userId, String deleteKeyword);

}
