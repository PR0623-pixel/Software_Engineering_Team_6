package com.team6.voca.dto.quiz;

import java.util.List;

// 파일 경로: src/main/java/com/team6/voca/dto/quiz/QuizSubmitRequestDto.java
// [정보은닉] 클라이언트로부터 채점에 필요한 단어 ID와 제출 답안만 전달받습니다.
public record QuizSubmitRequestDto(
        Long userId, // 이후 Spring Security 인증 객체(@AuthenticationPrincipal)로 대체 가능
        Integer totalQuestionCount,
        Boolean timeExpired,
        List<QuizAnswerDto> answers
) {
    // [모듈화] 문제별 제출 데이터를 작은 DTO로 분리합니다.
    public record QuizAnswerDto(
            Long wordId,
            String submittedWord
    ) {}
}
