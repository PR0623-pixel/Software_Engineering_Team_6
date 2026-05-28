package com.team6.voca.domain.word;
import java.util.ArrayList;
import java.util.List;

import com.team6.voca.domain.BaseTimeEntity;

// JPA 어노테이션
import jakarta.persistence.*;

//Getter와 Setter를 위한 Lombok import
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "words")
@Getter
// [캡슐화] 무분별한 객체 생성을 막기 위해 기본 생성자의 접근 제어자를 protected로 제한하여 객체의 안전성을 높입니다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Word extends BaseTimeEntity { // 공동 시간 필드 상속을 위한 BaseTimeEntity 설계.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // [정보은닉] 모든 필드를 private으로 선언하여 외부에서 직접 접근(수정)하지 못하도록 철저히 숨깁니다.
    @Column(name = "english_word", nullable = false, length = 100)
    private String englishWord;

    @Column(name = "korean_meaning", nullable = false, length = 100)
    private String koreanMeaning;

    // [모듈화/다형성] 품사와 난이도를 일반 String이 아닌 Enum 타입으로 분리하여 데이터 정합성을 보장하고, 
    // 향후 로직 확장에 유연하게 대처할 수 있도록 설계합니다.
    @Enumerated(EnumType.STRING)
    @Column(name = "part_of_speech", nullable = false)
    private PartOfSpeech partOfSpeech;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private WordLevel level;

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WordExample> examples = new ArrayList<>();

    @Builder
    public Word(String englishWord, String koreanMeaning, PartOfSpeech partOfSpeech, WordLevel level) {
        this.englishWord = englishWord;
        this.koreanMeaning = koreanMeaning;
        this.partOfSpeech = partOfSpeech;
        this.level = level;
    }

    // [캡슐화] 무분별한 @Setter 사용을 금지하고, 비즈니스 로직(예: 뜻 수정)은 
    // 엔티티 내부의 명확한 의도를 가진 메서드를 통해서만 수행하도록 상태를 관리합니다.
    public void updateMeaning(String newMeaning) {
        this.koreanMeaning = newMeaning;
    }

    public void update(String englishWord, String koreanMeaning, PartOfSpeech partOfSpeech, WordLevel level) {
        this.englishWord = englishWord;
        this.koreanMeaning = koreanMeaning;
        this.partOfSpeech = partOfSpeech;
        this.level = level;
    }

    public void addExample(WordExample example){
        this.examples.add(example);
        example.setWord(this);
    }

    public void replaceExamples(List<WordExample> newExamples) {
        this.examples.clear();
        for (WordExample example : newExamples) {
            example.setWord(this);
            this.examples.add(example);
        }
    }
}
