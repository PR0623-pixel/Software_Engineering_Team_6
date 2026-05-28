package com.team6.voca.domain.shop;

import com.team6.voca.domain.BaseTimeEntity;
import com.team6.voca.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_purchases")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPurchase extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_item_id", nullable = false)
    private ShopItem shopItem;

    @Column(name = "price_paid", nullable = false)
    private int pricePaid;

    @Builder
    public UserPurchase(User user, ShopItem shopItem, int pricePaid) {
        this.user = user;
        this.shopItem = shopItem;
        this.pricePaid = pricePaid;
    }
}
