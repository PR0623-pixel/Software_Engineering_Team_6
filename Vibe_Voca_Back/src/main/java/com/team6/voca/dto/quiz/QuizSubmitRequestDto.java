package com.team6.voca.dto.quiz;

import java.util.List;

// 파일 경로: src/main/java/com/team6/voca/dto/quiz/QuizSubmitRequestDto.java
public record QuizSubmitRequestDto(
        Long userId,
        Integer totalQuestionCount,
        Boolean timeExpired,
        List<QuizAnswerDto> answers
) {
    public record QuizAnswerDto(
            Long wordId,
            String submittedAnswer
    ) {}
}