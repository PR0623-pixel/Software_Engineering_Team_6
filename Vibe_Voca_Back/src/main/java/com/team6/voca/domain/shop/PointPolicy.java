package com.team6.voca.domain.shop;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "point_policies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_key", nullable = false, unique = true, length = 50)
    private String policyKey;

    @Column(nullable = false)
    private int points;

    @Column(length = 255)
    private String description;

    public void updatePoints(int points) {
        this.points = points;
    }
}
