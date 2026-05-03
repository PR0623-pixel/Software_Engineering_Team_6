package com.team6.voca.controller;

import com.team6.voca.dto.word.WordCreateRequest;
import com.team6.voca.dto.word.WordResponseDto;
import com.team6.voca.dto.word.WordUpdateRequest;
import com.team6.voca.service.WordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// [모듈화] 클라이언트와의 HTTP 통신만을 전담하는 모듈로 분리
@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    // [캡슐화] 단어 목록을 가져오는 복잡한 비즈니스 로직과 트랜잭션 처리는 wordService 내부로 캡슐화되어 있습니다.
    // Controller는 단지 메서드를 호출하고 그 결과를 HTTP 형식(ResponseEntity)으로 포장하는 역할만 수행합니다.
    @GetMapping
    public ResponseEntity<List<WordResponseDto>> getAllWords() {
        List<WordResponseDto> words = wordService.getAllWords();

        // [정보은닉] DB의 테이블 구조가 그대로 반영된 Entity(Word)를 반환하지 않고,
        // 클라이언트에게 꼭 필요한 정보만 담긴 DTO(WordResponseDto)를 반환하여 내부 데이터 구조를 숨깁니다.
        return ResponseEntity.ok(words);
    }

    @PostMapping
    public ResponseEntity<WordResponseDto> createWord(@Valid @RequestBody WordCreateRequest request) {
        return ResponseEntity.ok(wordService.createWord(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WordResponseDto> updateWord(
            @PathVariable Long id,
            @Valid @RequestBody WordUpdateRequest request) {
        return ResponseEntity.ok(wordService.updateWord(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
        wordService.deleteWord(id);
        return ResponseEntity.noContent().build();
    }
}