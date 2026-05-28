package com.team6.voca.dto.word;

import com.team6.voca.domain.word.WordExample;

public record WordExampleDto(
        Long id,
        String exampleSentence,
        String koreanTranslation
) {
    public static WordExampleDto from(WordExample example) {
        return new WordExampleDto(
                example.getId(),
                example.getExampleSentence(),
                example.getKoreanTranslation()
        );
    }
}
