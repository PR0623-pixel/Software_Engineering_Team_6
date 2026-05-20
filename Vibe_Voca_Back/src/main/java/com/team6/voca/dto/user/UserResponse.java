package com.team6.voca.dto.user;

import com.team6.voca.domain.user.User;
import com.team6.voca.domain.user.UserStatus;

// 파일 경로: src/main/java/com/team6/voca/dto/user/UserResponse.java
public class UserResponse {

    private static final int DEFAULT_LEVEL = 1;
    private static final int DEFAULT_POINTS = 0;

    private final Long id;
    private final String email;
    private final String nickname;
    private final UserStatus status;
    private final String profileImg;
    private final Integer level;
    private final Integer points;

    public UserResponse(
            Long id,
            String email,
            String nickname,
            UserStatus status,
            String profileImg,
            Integer level,
            Integer points
    ) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.status = status;
        this.profileImg = profileImg;
        this.level = level;
        this.points = points;
    }

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getStatus(),
                user.getProfileImg(),
                resolveLevel(user),
                resolvePoints(user)
        );
    }

    // [캡슐화] 수준 점검 테스트가 붙으면 User의 실제 레벨 필드를 여기서 연결하면 됩니다.
    private static Integer resolveLevel(User user) {
        return DEFAULT_LEVEL;
    }

    // [캡슐화] 퀴즈 정답률 기반 포인트 정책이 붙으면 계산/조회 값을 여기서 연결하면 됩니다.
    private static Integer resolvePoints(User user) {
        return DEFAULT_POINTS;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
    public UserStatus getStatus() { return status; }
    public String getProfileImg() { return profileImg; }
    public Integer getLevel() { return level; }
    public Integer getPoints() { return points; }
}
