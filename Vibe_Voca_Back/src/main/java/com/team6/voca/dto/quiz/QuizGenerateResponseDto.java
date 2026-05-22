package com.team6.voca.dto.quiz;

import java.util.List;

// 파일 경로: src/main/java/com/team6/voca/dto/quiz/QuizGenerateResponseDto.java
// [정보은닉] 프론트엔드가 필요한 퀴즈 문항과 제한 시간만 외부로 전달합니다.
public record QuizGenerateResponseDto(
        List<QuizQuestionResponseDto> questions,
        Integer timerSeconds,
        Boolean fixedTimer
) {
    public static QuizGenerateResponseDto of(
            List<QuizQuestionResponseDto> questions,
            Integer timerSeconds,
            Boolean fixedTimer
    ) {
        return new QuizGenerateResponseDto(questions, timerSeconds, fixedTimer);
    }
}
