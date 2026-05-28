package com.team6.voca.service;

import com.team6.voca.common.exception.NotFoundException;
import com.team6.voca.domain.shop.PointPolicy;
import com.team6.voca.domain.shop.ShopItem;
import com.team6.voca.domain.shop.UserPurchase;
import com.team6.voca.domain.user.User;
import com.team6.voca.dto.shop.*;
import com.team6.voca.repository.PointPolicyRepository;
import com.team6.voca.repository.ShopItemRepository;
import com.team6.voca.repository.UserPurchaseRepository;
import com.team6.voca.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final ShopItemRepository shopItemRepository;
    private final UserPurchaseRepository userPurchaseRepository;
    private final PointPolicyRepository pointPolicyRepository;
    private final UserRepository userRepository;

    public List<ShopItemResponse> getActiveItems() {
        return shopItemRepository.findByDeletedFalse().stream()
                .map(ShopItemResponse::from)
                .collect(Collectors.toList());
    }

    public List<ShopItemResponse> getAllItems() {
        return shopItemRepository.findAll().stream()
                .map(ShopItemResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShopItemResponse createItem(ShopItemCreateRequest req) {
        ShopItem item = ShopItem.builder()
                .name(req.name())
                .description(req.description())
                .price(req.price())
                .imageUrl(req.imageUrl())
                .build();
        return ShopItemResponse.from(shopItemRepository.save(item));
    }

    @Transactional
    public ShopItemResponse updateItem(Long itemId, ShopItemUpdateRequest req) {
        ShopItem item = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("아이템을 찾을 수 없습니다."));
        item.update(req.name(), req.description(), req.price(), req.imageUrl());
        return ShopItemResponse.from(item);
    }

    @Transactional
    public void deleteItem(Long itemId) {
        ShopItem item = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("아이템을 찾을 수 없습니다."));
        item.softDelete();
    }

    @Transactional
    public void purchaseItem(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
        ShopItem item = shopItemRepository.findById(itemId)
                .filter(i -> !i.isDeleted())
                .orElseThrow(() -> new NotFoundException("아이템을 찾을 수 없습니다."));

        if (userPurchaseRepository.existsByUserIdAndShopItemId(userId, itemId)) {
            throw new IllegalArgumentException("이미 구매한 아이템입니다.");
        }
        if (user.getPoints() < item.getPrice()) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        user.deductPoints(item.getPrice());
        userPurchaseRepository.save(UserPurchase.builder()
                .user(user)
                .shopItem(item)
                .pricePaid(item.getPrice())
                .build());
    }

    public List<PurchaseHistoryResponse> getPurchaseHistory(Long userId) {
        return userPurchaseRepository.findByUserId(userId).stream()
                .map(PurchaseHistoryResponse::from)
                .collect(Collectors.toList());
    }

    public List<PointPolicyResponse> getPolicies() {
        return pointPolicyRepository.findAll().stream()
                .map(PointPolicyResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public PointPolicyResponse updatePolicy(Long policyId, PointPolicyUpdateRequest req) {
        PointPolicy policy = pointPolicyRepository.findById(policyId)
                .orElseThrow(() -> new NotFoundException("정책을 찾을 수 없습니다."));
        policy.updatePoints(req.points());
        return PointPolicyResponse.from(policy);
    }
}
