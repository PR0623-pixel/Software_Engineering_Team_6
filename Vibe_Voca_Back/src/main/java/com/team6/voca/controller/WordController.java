package com.team6.voca.controller;

import com.team6.voca.dto.word.WordCreateRequest;
import com.team6.voca.dto.word.WordDetailResponseDto;
import com.team6.voca.dto.word.WordResponseDto;
import com.team6.voca.dto.word.WordUpdateRequest;
import com.team6.voca.service.WordService;
import com.team6.voca.domain.user.UserRole;
import com.team6.voca.common.exception.UnauthorizedException;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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

    // [모듈화] 관리자 권한 체크 로직을 별도의 private 메서드로 분리하여 코드의 재사용성을 높였습니다.
    private void validateAdminRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || !UserRole.ADMIN.name().equals(session.getAttribute("role"))) {
            throw new UnauthorizedException("해당 기능을 수행할 관리자 권한이 없습니다.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<WordDetailResponseDto> getWordById(@PathVariable Long id) {
        return ResponseEntity.ok(wordService.getWordById(id));
    }

    @GetMapping
    public ResponseEntity<List<WordResponseDto>> getAllWords() {
        return ResponseEntity.ok(wordService.getAllWords());
    }

    // 아래의 C, U, D API는 관리자만 접근 가능하도록 검증 로직 추가
    @PostMapping
    public ResponseEntity<WordResponseDto> createWord(
            @Valid @RequestBody WordCreateRequest request, 
            HttpServletRequest httpRequest) {
        validateAdminRole(httpRequest);
        return ResponseEntity.ok(wordService.createWord(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WordDetailResponseDto> updateWord(
            @PathVariable Long id,
            @Valid @RequestBody WordUpdateRequest request,
            HttpServletRequest httpRequest) {
        validateAdminRole(httpRequest);
        return ResponseEntity.ok(wordService.updateWord(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWord(
            @PathVariable Long id, 
            HttpServletRequest httpRequest) {
        validateAdminRole(httpRequest);
        wordService.deleteWord(id);
        return ResponseEntity.noContent().build();
    }
}