package com.team6.voca.domain.user;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    private String profileImg;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private UserLevel level = UserLevel.STARTER;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    // [캡슐화] 권한 필드를 추가하고 외부에서 함부로 수정하지 못하도록 접근 제어자를 private으로 설정합니다. (SPRINT2)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER; // 기본값은 USER

    public User() {}

    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    // Getter & Setter
    public Long getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public void changePassword(String encodePassword){
        this.password = encodePassword;
    }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getProfileImg() { return profileImg; }
    public void setProfileImg(String profileImg) { this.profileImg = profileImg; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public UserLevel getLevel() { return level; }
    public void setLevel(UserLevel level) { this.level = level; }

    // Admin에 관한 Getter/Setter 설정

    public UserRole getRole() {return role;}
    public void setRole(UserRole role) {this.role = role;}

    @Column(nullable = false)
    private int points = 0;

    public int getPoints() { return points; }

    public void addPoints(int amount) { this.points += amount; }

    public void deductPoints(int amount) {
        if (this.points < amount) throw new IllegalArgumentException("포인트가 부족합니다.");
        this.points -= amount;
    }
}
