// src/main/java/com/team6/voca/domain/QuizResult.java
package com.team6.voca.domain.quiz;

import com.team6.voca.domain.BaseTimeEntity;
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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    // ⭐ [다형성] 양방향 매핑을 위한 리스트 선언
    @OneToMany(mappedBy = "quizResult", cascade = CascadeType.ALL)
    private List<ErrorNote> errorNotes = new ArrayList<>();

    @Builder
    public QuizResult(Long userId, Integer score, Integer totalQuestions) {
        this.userId = userId;
        this.score = score;
        this.totalQuestions = totalQuestions;
    }

    // ⭐ [캡슐화] null 방어 로직이 포함된 안전한 양방향 매핑 메서드
    public void addErrorNote(ErrorNote errorNote) {
        // Builder로 인해 리스트가 초기화되지 않았을 경우를 대비한 방어 코드
        if (this.errorNotes == null) {
            this.errorNotes = new ArrayList<>();
        }
        
        this.errorNotes.add(errorNote);
        errorNote.setQuizResult(this); // ErrorNote.java에 이 메서드가 있어야 에러가 나지 않습니다.
    }
}