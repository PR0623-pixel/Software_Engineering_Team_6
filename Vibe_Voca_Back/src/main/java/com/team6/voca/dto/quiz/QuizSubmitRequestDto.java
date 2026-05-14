package com.team6.voca.dto.quiz;

import java.util.List;

public record QuizSubmitRequestDto(
    Long userId, 
    List<AnswerDto> answers // 배열 이름 확인 (ex: userAnswers?)
) {
    public record AnswerDto(
        Long wordId, 
        String submittedAnswer // 프론트엔드에서 보내는 변수명이 userAnswer라면 변경 필요
    ) {}
}