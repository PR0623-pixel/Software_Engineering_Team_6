package com.team6.voca.dto.word;

import com.team6.voca.domain.word.Word;

public record WordResponseDto(
        Long id, 
        String englishWord, 
        String koreanMeaning
) {
    public static WordResponseDto from(Word word) {
        return new WordResponseDto(
                word.getId(),
                word.getEnglishWord(),
                word.getKoreanMeaning()
        );
    }
}