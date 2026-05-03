package com.team6.voca.dto.quiz;

import java.util.List;

// [정보은닉] 클라이언트로부터 채점에 필요한 '단어 ID'와 '유저가 입력한 영어 단어'만 전달받습니다.
public record QuizSubmitRequestDto(
        Long userId, // 향후 Spring Security 인증 객체(@AuthenticationPrincipal)로 대체 가능
        List<QuizAnswerDto> answers
) {
    // 내부 Record를 사용하여 문제별 제출 데이터를 깔끔하게 모듈화합니다.
    public record QuizAnswerDto(
            Long wordId,
            String submittedWord
    ) {}
}
