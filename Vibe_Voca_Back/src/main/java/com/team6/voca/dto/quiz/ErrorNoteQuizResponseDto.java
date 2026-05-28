package com.team6.voca.dto.quiz;

import java.util.List;

/**
 * [모듈화] 오답노트 재테스트 전용 응답 DTO입니다.
 * 일반 퀴즈 객체와 완전히 분리되어, 퀴즈 도메인의 변경이 오답노트에 영향을 주지 않도록 결합도를 낮춥니다.
 */
// [정보은닉] Java 17의 Record를 사용하여 한 번 생성된 응답 데이터가 
// 컨트롤러나 네트워크 전송 단계에서 임의로 수정되는 것을 원천 차단(불변 객체)합니다.
public record ErrorNoteQuizResponseDto(
        Long wordId,            // 정답 채점을 위해 단어의 고유 ID를 전달
        String questionMeaning, // 출제될 문제 (한국어 뜻)
        String exampleSentence, // 복습을 돕기 위해 DB에서 가져온 예문 (오답노트 특화 필드)
        List<String> options,   // 사지선다 보기 (정답 + 랜덤 오답 3개)
        String correctAnswer    // 정답 영어 단어
) {
    // [캡슐화] 객체 생성 책임을 Record 내부의 정적 팩토리 메서드(Static Factory Method)로 
    // 집중시켜 응집도를 높이고 외부에서의 무분별한 생성을 제어합니다.
    public static ErrorNoteQuizResponseDto of(
            Long wordId, 
            String questionMeaning, 
            String exampleSentence, 
            List<String> options, 
            String correctAnswer) {
        
        return new ErrorNoteQuizResponseDto(
                wordId, 
                questionMeaning, 
                exampleSentence, 
                options, 
                correctAnswer
        );
    }
}