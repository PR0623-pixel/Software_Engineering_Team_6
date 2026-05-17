package com.team6.voca.controller;

import com.team6.voca.common.exception.UnauthorizedException;
import com.team6.voca.domain.user.UserRole;
import com.team6.voca.dto.shop.*;
import com.team6.voca.service.ShopService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    private Long requireLogin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        return (Long) session.getAttribute("userId");
    }

    private void requireAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || !UserRole.ADMIN.name().equals(session.getAttribute("role"))) {
            throw new UnauthorizedException("해당 기능을 수행할 관리자 권한이 없습니다.");
        }
    }

    // ── 사용자 엔드포인트 ──────────────────────────────────────────────

    @GetMapping("/items")
    public ResponseEntity<List<ShopItemResponse>> getItems() {
        return ResponseEntity.ok(shopService.getActiveItems());
    }

    @PostMapping("/items/{itemId}/purchase")
    public ResponseEntity<Void> purchase(@PathVariable Long itemId, HttpServletRequest req) {
        Long userId = requireLogin(req);
        shopService.purchaseItem(userId, itemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/purchases")
    public ResponseEntity<List<PurchaseHistoryResponse>> myPurchases(HttpServletRequest req) {
        Long userId = requireLogin(req);
        return ResponseEntity.ok(shopService.getPurchaseHistory(userId));
    }

    // ── 관리자 엔드포인트 ─────────────────────────────────────────────

    @GetMapping("/admin/items")
    public ResponseEntity<List<ShopItemResponse>> getAllItems(HttpServletRequest req) {
        requireAdmin(req);
        return ResponseEntity.ok(shopService.getAllItems());
    }

    @PostMapping("/admin/items")
    public ResponseEntity<ShopItemResponse> createItem(
            @RequestBody ShopItemCreateRequest request, HttpServletRequest req) {
        requireAdmin(req);
        return ResponseEntity.ok(shopService.createItem(request));
    }

    @PutMapping("/admin/items/{itemId}")
    public ResponseEntity<ShopItemResponse> updateItem(
            @PathVariable Long itemId,
            @RequestBody ShopItemUpdateRequest request,
            HttpServletRequest req) {
        requireAdmin(req);
        return ResponseEntity.ok(shopService.updateItem(itemId, request));
    }

    @DeleteMapping("/admin/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId, HttpServletRequest req) {
        requireAdmin(req);
        shopService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/policies")
    public ResponseEntity<List<PointPolicyResponse>> getPolicies(HttpServletRequest req) {
        requireAdmin(req);
        return ResponseEntity.ok(shopService.getPolicies());
    }

    @PutMapping("/admin/policies/{policyId}")
    public ResponseEntity<PointPolicyResponse> updatePolicy(
            @PathVariable Long policyId,
            @RequestBody PointPolicyUpdateRequest request,
            HttpServletRequest req) {
        requireAdmin(req);
        return ResponseEntity.ok(shopService.updatePolicy(policyId, request));
    }
}
