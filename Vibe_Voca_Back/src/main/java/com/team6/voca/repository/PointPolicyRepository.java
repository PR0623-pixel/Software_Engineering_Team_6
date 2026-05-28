package com.team6.voca.repository;

import com.team6.voca.domain.shop.PointPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {
    Optional<PointPolicy> findByPolicyKey(String policyKey);
}
