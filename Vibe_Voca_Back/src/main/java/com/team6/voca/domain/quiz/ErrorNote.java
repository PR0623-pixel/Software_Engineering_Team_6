// src/main/java/com/team6/voca/domain/ErrorNote.java
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

    @Builder
    public ErrorNote(Word word, String submittedAnswer) {
        this.word = word;
        this.submittedAnswer = submittedAnswer;
    }

    // [캡슐화] 이 메서드가 반드시 존재해야 QuizResult.java에서 호출 가능.
    public void setQuizResult(QuizResult quizResult) {
        this.quizResult = quizResult;
    }
}