package com.team6.voca.service;

import org.springframework.stereotype.Service;

import com.team6.voca.common.exception.NotFoundException;
import com.team6.voca.common.exception.UnauthorizedException;
import com.team6.voca.domain.user.User;
import com.team6.voca.domain.user.UserRole;
import com.team6.voca.dto.auth.LoginRequest;
import com.team6.voca.dto.user.UserResponse;
import com.team6.voca.repository.UserRepository;


// 11/23일 추가 작업, AuthService의 추가 작성 필요에 따라 작성.
@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    //로그인 기능 (클라이언트)
    public UserResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new NotFoundException("존재하지 않는 이메일입니다."));

        if(!user.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException("잘못된 비밀번호입니다.");
        }
        
        // [모듈화] 관리자 모드로 로그인을 시도했는데, 실제 권한이 관리자가 아닌 경우 예외 처리
        if (request.isAdminLogin() && user.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("관리자 권한이 없습니다.");
        }

        return UserResponse.from(user);
    }


}
