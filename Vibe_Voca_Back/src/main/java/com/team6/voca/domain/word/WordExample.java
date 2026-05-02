// src/main/java/com/team6/voca/domain/word/WordExample.java
package com.team6.voca.domain.word;

import com.team6.voca.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "word_examples")
@Getter
// [캡슐화] 무분별한 객체 생성을 막기 위해 기본 생성자를 protected로 제한합니다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WordExample extends BaseTimeEntity { // [상속] 생성일/수정일 자동 추적을 위해 BaseTimeEntity 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // [정보은닉] 모든 필드를 private으로 선언하여 외부의 직접적인 접근을 차단합니다.
    @Column(name = "example_sentence", nullable = false, length = 255)
    private String exampleSentence;

    @Column(name = "korean_translation", nullable = false, length = 255)
    private String koreanTranslation;

    // [모듈화] 단어(Word)와 예문(WordExample)을 분리하여 각각의 책임(테이블)을 나누고 다대일(N:1) 관계로 연결합니다.
    // 성능 최적화를 위해 fetch 속성을 지연 로딩(LAZY)으로 설정합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Builder
    public WordExample(String exampleSentence, String koreanTranslation, Word word) {
        this.exampleSentence = exampleSentence;
        this.koreanTranslation = koreanTranslation;
        this.word = word;
    }

    // [캡슐화] Word 클래스의 addExample 메서드에서 연관관계를 안전하게 매핑하기 위해 제공되는 메서드입니다.
    public void setWord(Word word) {
        this.word = word;
    }
}