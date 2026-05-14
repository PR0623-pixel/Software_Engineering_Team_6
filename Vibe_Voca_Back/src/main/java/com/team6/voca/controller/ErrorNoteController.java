package com.team6.voca.controller;

// [수정] 기존 QuizQuestionResponseDto 대신 오답노트 전용 DTO를 import 합니다.
import com.team6.voca.dto.ErrorNote.ErrorNoteQuizResponseDto;
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
     */
    @GetMapping
    public ResponseEntity<List<?>> getErrorNotes(@RequestParam Long userId) {
        return ResponseEntity.ok(errorNoteService.getErrorNotes(userId));
    }

    /**
     * [정보은닉] 오답노트 메모 수정 (Update)
     */
    @PatchMapping("/{id}/memo")
    public ResponseEntity<Void> updateMemo(@PathVariable Long id, @RequestBody String memo) {
        errorNoteService.updateErrorNoteMemo(id, memo);
        return ResponseEntity.ok().build();
    }

    /**
     * [모듈화] 오답노트 단건 삭제 (Delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteErrorNote(@PathVariable Long id) {
        errorNoteService.deleteErrorNote(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * [재사용성/모듈화] 오답노트 기반 재테스트 생성
     * 🚨 [수정 완료] 반환 타입을 ErrorNoteQuizResponseDto로 맞추고, userId 파라미터를 추가했습니다.
     */
    @GetMapping("/quiz")
    public ResponseEntity<List<ErrorNoteQuizResponseDto>> generateErrorNoteQuiz(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") int count) {
        
        // [캡슐화] 내부적인 퀴즈 생성 알고리즘은 Service 계층에 숨기고 전용 DTO로 반환받습니다.
        List<ErrorNoteQuizResponseDto> quizData = errorNoteService.generateQuizFromErrors(userId, count);
        return ResponseEntity.ok(quizData);
    }
}