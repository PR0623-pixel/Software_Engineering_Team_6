package com.team6.voca.dto.user;

import com.team6.voca.domain.user.User;
import com.team6.voca.domain.user.UserStatus;
import com.team6.voca.domain.user.UserLevel;

public class UserResponse {

    private final Long id;
    private final String email;
    private final String nickname;
    private final UserLevel level;
    private final UserStatus status;
    private final String profileImg;

    public UserResponse(Long id, String email, String nickname,
                        UserLevel level, UserStatus status, String profileImg) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.level = level;
        this.status = status;
        this.profileImg = profileImg;
    }

    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            user.getLevel(),
            user.getStatus(),
            user.getProfileImg()
        );
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
    public UserLevel getLevel() { return level; }
    public UserStatus getStatus() { return status; }
    public String getProfileImg() { return profileImg; }
}

