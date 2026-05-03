package com.team6.voca.dto.word;

import com.team6.voca.domain.word.PartOfSpeech;
import com.team6.voca.domain.word.Word;
import com.team6.voca.domain.word.WordLevel;

public record WordResponseDto(
        Long id,
        String englishWord,
        String koreanMeaning,
        PartOfSpeech partOfSpeech,
        WordLevel level
) {
    public static WordResponseDto from(Word word) {
        return new WordResponseDto(
                word.getId(),
                word.getEnglishWord(),
                word.getKoreanMeaning(),
                word.getPartOfSpeech(),
                word.getLevel()
        );
    }
}