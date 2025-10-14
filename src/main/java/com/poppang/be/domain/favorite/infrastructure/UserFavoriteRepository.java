package com.poppang.be.domain.favorite.infrastructure;

import com.poppang.be.domain.favorite.entity.UserFavorite;
import com.poppang.be.domain.popup.entity.Popup;
import com.poppang.be.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {

    boolean existsByUserAndPopup(Users user, Popup popup);

    Optional<UserFavorite> findByUserUuidAndPopupUuid(String userUuid, String popupUuid);

    long countByPopupUuid(String popupUuid);

    List<UserFavorite> findAllByUserUuid(String userUuid);

}
