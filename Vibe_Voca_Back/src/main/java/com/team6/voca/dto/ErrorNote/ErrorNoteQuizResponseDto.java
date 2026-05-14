// src/main/java/com/team6/voca/dto/errornote/ErrorNoteQuizResponseDto.java
package com.team6.voca.dto.ErrorNote;

import java.util.List;

/**
 * [정보은닉] Record를 사용하여 재테스트 응답 데이터를 불변으로 관리합니다.
 * [모듈화] 퀴즈 도메인과 분리하여 오답노트만의 고유한 필드(예문 등)를 유지합니다.
 */
public record ErrorNoteQuizResponseDto(
    Long wordId,
    String spelling,
    String meaning,
    List<String> options,
    String exampleSentence,    // DB 내 WordExample 연동
    String userMemo            // 사용자가 남긴 오답 메모
) {
    public static ErrorNoteQuizResponseDto of(
            Long wordId, String spelling, String meaning, 
            List<String> options, String sentence, String userMemo) {
        return new ErrorNoteQuizResponseDto(wordId, spelling, meaning, options, sentence, userMemo);
    }
}