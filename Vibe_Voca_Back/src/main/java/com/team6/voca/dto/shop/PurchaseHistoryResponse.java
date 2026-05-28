package com.team6.voca.dto.shop;

import com.team6.voca.domain.shop.UserPurchase;

import java.time.LocalDateTime;

public class PurchaseHistoryResponse {

    private final Long id;
    private final Long shopItemId;
    private final String itemName;
    private final String itemImageUrl;
    private final int pricePaid;
    private final LocalDateTime purchasedAt;

    public PurchaseHistoryResponse(Long id, Long shopItemId, String itemName, String itemImageUrl,
                                   int pricePaid, LocalDateTime purchasedAt) {
        this.id = id;
        this.shopItemId = shopItemId;
        this.itemName = itemName;
        this.itemImageUrl = itemImageUrl;
        this.pricePaid = pricePaid;
        this.purchasedAt = purchasedAt;
    }

    public static PurchaseHistoryResponse from(UserPurchase purchase) {
        return new PurchaseHistoryResponse(
            purchase.getId(),
            purchase.getShopItem().getId(),
            purchase.getShopItem().getName(),
            purchase.getShopItem().getImageUrl(),
            purchase.getPricePaid(),
            purchase.getCreatedAt()
        );
    }

    public Long getId() { return id; }
    public Long getShopItemId() { return shopItemId; }
    public String getItemName() { return itemName; }
    public String getItemImageUrl() { return itemImageUrl; }
    public int getPricePaid() { return pricePaid; }
    public LocalDateTime getPurchasedAt() { return purchasedAt; }
}
