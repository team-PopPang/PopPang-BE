package com.poppang.be.domain.users.infrastructure;

import com.poppang.be.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUid(String uid);

    boolean existsByNickname(String nickname);

    Optional<Users> findByUidAndDeletedFalse(String uid);

}
