package com.team6.voca.dto.user;

import com.team6.voca.domain.user.*;

// 파일 경로: src/main/java/com/team6/voca/dto/user/UserResponse.java
public class UserResponse {

    private final Long id;
    private final String email;
    private final String nickname;
    private final UserLevel level;
    private final UserStatus status;
    private final String profileImg;
    private UserRole role;
    private final int points;

    public UserResponse(Long id, String email, String nickname,
                        UserLevel level, UserStatus status, String profileImg, UserRole role, int points) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.level = level;
        this.status = status;
        this.profileImg = profileImg;
        this.role = role;
        this.points = points;
    }

    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            user.getLevel(),
            user.getStatus(),
            user.getProfileImg(),
            user.getRole(),
            user.getPoints()
        );
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
    public UserLevel getLevel() { return level; }
    public UserStatus getStatus() { return status; }
    public String getProfileImg() { return profileImg; }
    public UserRole getRole() { return role; }
    public int getPoints() { return points; }
}
