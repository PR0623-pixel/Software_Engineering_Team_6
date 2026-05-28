package com.team6.voca.dto.shop;

import com.team6.voca.domain.shop.ShopItem;

public class ShopItemResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final int price;
    private final String imageUrl;
    private final boolean deleted;

    public ShopItemResponse(Long id, String name, String description, int price, String imageUrl, boolean deleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.deleted = deleted;
    }

    public static ShopItemResponse from(ShopItem item) {
        return new ShopItemResponse(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getPrice(),
            item.getImageUrl(),
            item.isDeleted()
        );
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public boolean isDeleted() { return deleted; }
}
