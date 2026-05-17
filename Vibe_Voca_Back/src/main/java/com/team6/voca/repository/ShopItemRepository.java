package com.team6.voca.repository;

import com.team6.voca.domain.shop.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {
    List<ShopItem> findByDeletedFalse();
}
