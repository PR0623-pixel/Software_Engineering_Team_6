package com.team6.voca.dto.quiz;

import java.util.List;

// [정보은닉] Java 17의 Record를 사용하여 객체의 상태를 불변(Immutable)으로 만듭니다. 
// 응답 데이터가 컨트롤러나 네트워크 단에서 임의로 수정되는 것을 원천 차단합니다.
public record QuizQuestionResponseDto(
        Long wordId,         // 정답 채점을 위해 단어의 고유 ID를 전달 (클라이언트는 정답 텍스트를 모름)
        String questionWord, // 출제될 문제 (예: 영단어)
        List<String> options // 4지 선다 보기 (정답 뜻 1개 + 오답 뜻 3개가 섞인 리스트)
) {
    // [모듈화] DTO 변환 책임을 Record 내부의 정적 팩토리 메서드로 분리하여 응집도를 높입니다.
    public static QuizQuestionResponseDto of(Long wordId, String questionWord, List<String> options) {
        return new QuizQuestionResponseDto(wordId, questionWord, options);
    }
}