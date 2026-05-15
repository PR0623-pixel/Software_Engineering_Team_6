package com.team6.voca.dto.ErrorNote;

import com.team6.voca.domain.quiz.ErrorNote;

/**
 * [모듈화] 오답노트 목록 조회 시 Entity 순환 참조를 방지하기 위한 전용 응답 DTO입니다.
 */
// [정보은닉] Java 17의 Record를 활용하여 데이터의 불변성을 보장하고 내부 Entity 스펙을 외부 API로부터 숨깁니다.
public record ErrorNoteListResponseDto(
        Long errorNoteId,        // 오답노트 고유 ID (수정/삭제 시 필요)
        Long wordId,             // 단어 고유 ID
        String englishWord,      // 영단어 (정답)
        String koreanMeaning,    // 뜻 (문제)
        String submittedAnswer,  // 사용자가 제출한 오답
        String memo              // 사용자 작성 메모
) {
    // [캡슐화] Entity를 DTO로 변환하는 복잡한 매핑 로직을 정적 팩토리 메서드 내부에 캡슐화합니다.
    public static ErrorNoteListResponseDto from(ErrorNote errorNote) {
        return new ErrorNoteListResponseDto(
                errorNote.getId(),
                errorNote.getWord().getId(),
                errorNote.getWord().getEnglishWord(),
                errorNote.getWord().getKoreanMeaning(),
                errorNote.getSubmittedAnswer(),
                errorNote.getMemo()
        );
    }
}