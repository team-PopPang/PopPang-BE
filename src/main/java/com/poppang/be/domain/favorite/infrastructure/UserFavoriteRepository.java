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

    Optional<UserFavorite> findByUserIdAndPopupId(Long userId, Long popupId);

    long countByPopupId(Long popupId);

    List<UserFavorite> findAllByUser_Id(Long userId);

}
