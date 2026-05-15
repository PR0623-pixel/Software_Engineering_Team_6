// src/main/java/com/team6/voca/domain/quiz/QuizResult.java
package com.team6.voca.domain.quiz;

import com.team6.voca.domain.BaseTimeEntity;
import com.team6.voca.domain.user.User; // User import 추가
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_results")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizResult extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // [수정 1] Long userId 대신 다대일(N:1) 객체 매핑으로 변경하여 일관성 확보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // [수정 3] Nullable이 불가능한 속성이므로 Integer 객체 대신 int 원시 타입 사용
    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "total_questions", nullable = false)
    private int totalQuestions;

    // [수정 2] Service 로직과 연동되기 위해 누락되었던 correctCount 필드 추가
    @Column(name = "correct_count", nullable = false)
    private int correctCount;

    // ⭐ [다형성] 양방향 매핑을 위한 리스트 선언
    @OneToMany(mappedBy = "quizResult", cascade = CascadeType.ALL)
    private List<ErrorNote> errorNotes = new ArrayList<>();

    @Builder
    public QuizResult(User user, int score, int totalQuestions, int correctCount) {
        this.user = user;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctCount = correctCount;
    }

    // ⭐ [캡슐화] null 방어 로직이 포함된 안전한 양방향 매핑 메서드
    public void addErrorNote(ErrorNote errorNote) {
        if (this.errorNotes == null) {
            this.errorNotes = new ArrayList<>();
        }
        this.errorNotes.add(errorNote);
        errorNote.setQuizResult(this);
    }

    // [캡슐화] 결과 업데이트용 편의 메서드 추가
    public void updateResult(int correctCount, int score) {
        this.correctCount = correctCount;
        this.score = score;
    }
}