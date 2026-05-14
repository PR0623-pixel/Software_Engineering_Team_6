// src/main/java/com/team6/voca/controller/ErrorNoteController.java
package com.team6.voca.controller;

import com.team6.voca.dto.quiz.QuizQuestionResponseDto;
import com.team6.voca.service.ErrorNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/error-notes")
@RequiredArgsConstructor
public class ErrorNoteController {

    private final ErrorNoteService errorNoteService;

    /**
     * [모듈화] 오답노트 목록 조회
     * 사용자가 오답노트 페이지 진입 시 전체 틀린 단어 리스트를 반환합니다.
     */
    @GetMapping
    public ResponseEntity<List<?>> getErrorNotes() {
        // TODO: ErrorNoteResponseDto 리스트 반환 로직 구현
        return ResponseEntity.ok().build();
    }

    /**
     * [정보은닉] 오답노트 메모 수정 (Update)
     * 특정 오답 단어에 대해 사용자가 작성한 메모를 수정합니다.
     */
    @PatchMapping("/{id}/memo")
    public ResponseEntity<Void> updateMemo(@PathVariable Long id, @RequestBody String memo) {
        errorNoteService.updateErrorNoteMemo(id, memo);
        return ResponseEntity.ok().build();
    }

    /**
     * [모듈화] 오답노트 단건 삭제 (Delete)
     * 암기가 완료된 단어를 오답노트에서 제거합니다.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteErrorNote(@PathVariable Long id) {
        errorNoteService.deleteErrorNote(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * [재사용성/모듈화] 오답노트 기반 재테스트 생성
     * 퀴즈 도메인이 아닌 오답노트 도메인에서 오답들로만 구성된 퀴즈를 생성하여 반환합니다.
     * 프론트엔드에서 '오답노트 재테스트' 버튼 클릭 시 이 엔드포인트를 호출합니다.
     */
    @GetMapping("/quiz")
    public ResponseEntity<List<QuizQuestionResponseDto>> generateErrorNoteQuiz(
            @RequestParam(defaultValue = "10") int count) {
        // [캡슐화] 내부적인 퀴즈 생성 알고리즘은 Service 계층에 숨기고 결과를 DTO로 반환합니다.
        List<QuizQuestionResponseDto> questions = errorNoteService.generateQuizFromErrors(count);
        return ResponseEntity.ok(questions);
    }
}