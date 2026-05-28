package com.team6.voca.dto.word;

import com.team6.voca.domain.word.PartOfSpeech;
import com.team6.voca.domain.word.Word;
import com.team6.voca.domain.word.WordLevel;

import java.util.List;

public record WordDetailResponseDto(
        Long id,
        String englishWord,
        String koreanMeaning,
        PartOfSpeech partOfSpeech,
        WordLevel level,
        List<WordExampleDto> examples
) {
    public static WordDetailResponseDto from(Word word) {
        List<WordExampleDto> examples = word.getExamples().stream()
                .map(WordExampleDto::from)
                .toList();
        return new WordDetailResponseDto(
                word.getId(),
                word.getEnglishWord(),
                word.getKoreanMeaning(),
                word.getPartOfSpeech(),
                word.getLevel(),
                examples
        );
    }
}
