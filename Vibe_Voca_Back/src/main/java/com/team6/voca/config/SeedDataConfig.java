package com.team6.voca.config;

import com.team6.voca.domain.user.User;
import com.team6.voca.domain.user.UserStatus;
import com.team6.voca.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

// 파일 경로: src/main/java/com/team6/voca/config/SeedDataConfig.java
@Configuration
public class SeedDataConfig {

    // [캡슐화] 개발/시연용 기본 계정 보정 로직을 애플리케이션 시작 시점으로 모읍니다.
    @Bean
    public ApplicationRunner seedDefaultUsers(UserRepository userRepository) {
        return args -> upsertDefaultUsers(userRepository);
    }

    // [정보은닉] 기본 계정 생성과 갱신 규칙을 외부 컨트롤러에 노출하지 않습니다.
    @Transactional
    protected void upsertDefaultUsers(UserRepository userRepository) {
        upsertUser(userRepository, "admin@vibevoca.com", "admin1234", "관리자");
        upsertUser(userRepository, "test@vibevoca.com", "user1234", "테스트유저");
    }

    // [모듈화] 같은 계정 보정 절차를 관리자/테스트 계정에 재사용합니다.
    private void upsertUser(
            UserRepository userRepository,
            String email,
            String password,
            String nickname
    ) {
        User user = userRepository.findByEmail(email).orElseGet(User::new);
        user.setEmail(email);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }
}
