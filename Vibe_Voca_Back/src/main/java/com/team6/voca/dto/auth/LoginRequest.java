package com.team6.voca.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    //관리자 모드 로그인 여부 판단 플래그
    private boolean adminLogin;

    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public boolean isAdminLogin() {return adminLogin;}
}
