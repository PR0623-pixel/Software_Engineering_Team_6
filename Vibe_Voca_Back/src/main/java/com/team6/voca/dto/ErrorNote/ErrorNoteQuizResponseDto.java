package com.team6.voca.dto.ErrorNote;

import java.util.List;

/**
 * [모듈화] 오답노트 재테스트 전용 응답 DTO입니다.
 * Service 로직의 요구사항에 맞춰 memo 필드를 추가하고 파라미터 순서를 동기화했습니다.
 */
// [정보은닉] Java 17의 Record를 사용하여 불변 객체를 생성하고 외부의 무단 수정을 차단합니다.
public record ErrorNoteQuizResponseDto(
        Long wordId,            // 정답 채점을 위해 단어의 고유 ID를 전달
        String correctAnswer,   // 정답 영어 단어 (Service의 word.getEnglishWord() 매핑)
        String questionMeaning, // 출제될 문제 (Service의 word.getKoreanMeaning() 매핑)
        List<String> options,   // 사지선다 보기
        String exampleSentence, // 예문
        String memo             // 사용자가 작성한 오답노트 메모 (추가됨)
) {
    // [캡슐화] 객체 생성 책임을 Record 내부의 정적 팩토리 메서드로 제한하여 일관성을 유지합니다.
    public static ErrorNoteQuizResponseDto of(
            Long wordId, 
            String correctAnswer, 
            String questionMeaning, 
            List<String> options, 
            String exampleSentence,
            String memo) {
        
        return new ErrorNoteQuizResponseDto(
                wordId, 
                correctAnswer, 
                questionMeaning, 
                options, 
                exampleSentence,
                memo
        );
    }
}