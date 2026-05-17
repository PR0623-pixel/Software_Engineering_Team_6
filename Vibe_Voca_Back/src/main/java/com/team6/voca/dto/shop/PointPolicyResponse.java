package com.team6.voca.dto.shop;

import com.team6.voca.domain.shop.PointPolicy;

public class PointPolicyResponse {

    private final Long id;
    private final String policyKey;
    private final int points;
    private final String description;

    public PointPolicyResponse(Long id, String policyKey, int points, String description) {
        this.id = id;
        this.policyKey = policyKey;
        this.points = points;
        this.description = description;
    }

    public static PointPolicyResponse from(PointPolicy policy) {
        return new PointPolicyResponse(
            policy.getId(),
            policy.getPolicyKey(),
            policy.getPoints(),
            policy.getDescription()
        );
    }

    public Long getId() { return id; }
    public String getPolicyKey() { return policyKey; }
    public int getPoints() { return points; }
    public String getDescription() { return description; }
}
