package com.team6.voca.dto.quiz;

// [정보은닉] Java 17의 Record를 사용하여 객체의 상태를 불변(Immutable)으로 만듭니다.
// 응답 데이터가 컨트롤러나 네트워크 단에서 임의로 수정되는 것을 원천 차단합니다.
public record QuizQuestionResponseDto(
        Long wordId,           // 정답 채점을 위해 단어의 고유 ID를 전달
        String questionMeaning, // 출제될 문제 (한국어 뜻)
        String correctAnswer    // 정답 영어 단어 (즉각 피드백용)
) {
    // [모듈화] DTO 변환 책임을 Record 내부의 정적 팩토리 메서드로 분리하여 응집도를 높입니다.
    public static QuizQuestionResponseDto of(Long wordId, String questionMeaning, String correctAnswer) {
        return new QuizQuestionResponseDto(wordId, questionMeaning, correctAnswer);
    }
}
