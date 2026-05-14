package com.team6.voca.dto.word;

import com.team6.voca.domain.word.PartOfSpeech;
import com.team6.voca.domain.word.WordLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WordUpdateRequest(
        @NotBlank String englishWord,
        @NotBlank String koreanMeaning,
        @NotNull PartOfSpeech partOfSpeech,
        @NotNull WordLevel level
) {}
