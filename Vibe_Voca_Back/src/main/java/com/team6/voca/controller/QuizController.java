// src/main/java/com/team6/voca/controller/QuizController.java
package com.team6.voca.controller;

import com.team6.voca.dto.quiz.QuizQuestionResponseDto;
import com.team6.voca.dto.quiz.QuizSubmitRequestDto;
import com.team6.voca.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/generate")
    public ResponseEntity<List<QuizQuestionResponseDto>> generateQuiz(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) String level) {
        return ResponseEntity.ok(quizService.generateQuestions(count, level));
    }

    // [모듈화] 일괄 제출(Batch)된 답안 데이터를 Service로 넘겨 채점을 지시합니다.
    @PostMapping("/submit")
    public ResponseEntity<Void> submitQuiz(@RequestBody QuizSubmitRequestDto request) {
        quizService.gradeAndSaveQuiz(request);
        return ResponseEntity.ok().build();
    }
}