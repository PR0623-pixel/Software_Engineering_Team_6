package com.team6.voca.repository;

import com.team6.voca.domain.shop.UserPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPurchaseRepository extends JpaRepository<UserPurchase, Long> {
    List<UserPurchase> findByUserId(Long userId);
    boolean existsByUserIdAndShopItemId(Long userId, Long shopItemId);
}
