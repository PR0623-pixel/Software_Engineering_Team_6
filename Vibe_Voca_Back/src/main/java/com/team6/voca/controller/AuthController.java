package com.team6.voca.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import com.team6.voca.common.exception.UnauthorizedException;
import com.team6.voca.dto.auth.LoginRequest;
import com.team6.voca.dto.user.UpdateProfileRequest;
import com.team6.voca.dto.user.UserRegisterRequest;
import com.team6.voca.dto.user.UserResponse;
import com.team6.voca.service.AuthService;
import com.team6.voca.service.UserService;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService){
        this.authService = authService;
        this.userService = userService;
    }

    //회원가입 구조
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest request){
        UserResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    //로그인 UserId 반환
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        
        UserResponse response = authService.login(request);

        // [정보은닉] 클라이언트에게 민감한 정보는 숨기고, 서버의 세션 공간에만 권한 상태를 안전하게 저장합니다.
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("userId", response.getId());
        session.setAttribute("role", response.getRole().name());
        
        return ResponseEntity.ok(response);
    }
    
    //마이페이지 조회
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(HttpServletRequest request) {
        
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        Long userId = (Long) session.getAttribute("userId");

        UserResponse response = userService.getMyInfo(userId);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
        HttpServletRequest request, @RequestBody UpdateProfileRequest updateRequest
    ){
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null){
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        Long userId = (Long) session.getAttribute("userId");

        UserResponse response = userService.updateProfile(userId, updateRequest);
        return ResponseEntity.ok(response);

    }
    
}
