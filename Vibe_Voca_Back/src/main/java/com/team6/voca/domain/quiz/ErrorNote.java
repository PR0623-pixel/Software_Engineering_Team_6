// src/main/java/com/team6/voca/domain/quiz/ErrorNote.java
package com.team6.voca.domain.quiz;

import com.team6.voca.domain.BaseTimeEntity;
import com.team6.voca.domain.word.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "error_notes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorNote extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_result_id", nullable = false)
    private QuizResult quizResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(name = "submitted_answer", length = 100)
    private String submittedAnswer;

    // 추가된 필드: 사용자가 오답노트에 자신만의 메모를 작성할 수 있도록 함
    @Column(name = "memo", length = 500)
    private String memo;

    @Builder
    public ErrorNote(Word word, String submittedAnswer, String memo) {
        this.word = word;
        this.submittedAnswer = submittedAnswer;
        this.memo = memo;
    }

    // [캡슐화] 이 메서드가 반드시 존재해야 QuizResult.java에서 양방향 연관관계 편의 메서드 호출 가능.
    public void setQuizResult(QuizResult quizResult) {
        this.quizResult = quizResult;
    }

    // [정보은닉 및 캡슐화] 외부에서 직접 필드에 접근하지 못하게 하고, 메서드를 통해 상태를 변경하도록 설계
    public void updateMemo(String memo) {
        this.memo = memo;
    }
}