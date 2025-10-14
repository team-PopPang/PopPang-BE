package com.poppang.be.domain.keyword.infrastructure;

import com.poppang.be.domain.keyword.entity.UserAlertKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAlertKeywordRepository extends JpaRepository<UserAlertKeyword, Long> {

    List<UserAlertKeyword> findAllByUserUuid(String userUuid);

    Optional<UserAlertKeyword> findByUserUuidAndAlertKeyword(String userUuid, String deleteKeyword);

}
