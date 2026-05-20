package com.team6.voca.controller;

import com.team6.voca.dto.quiz.QuizGenerateResponseDto;
import com.team6.voca.dto.quiz.QuizSubmitRequestDto;
import com.team6.voca.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 파일 경로: src/main/java/com/team6/voca/controller/QuizController.java
@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/generate")
    public ResponseEntity<QuizGenerateResponseDto> generateQuiz(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) String level,
            @RequestParam(defaultValue = "quiz") String quizType
    ) {
        return ResponseEntity.ok(quizService.generateQuiz(count, level, quizType));
    }

    // [모듈화] 일괄 제출 답안 데이터를 Service로 넘겨 채점하도록 지시합니다.
    @PostMapping("/submit")
    public ResponseEntity<Void> submitQuiz(@RequestBody QuizSubmitRequestDto request) {
        quizService.gradeAndSaveQuiz(request);
        return ResponseEntity.ok().build();
    }
}
