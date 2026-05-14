// src/main/java/com/team6/voca/domain/word/service/WordService.java
package com.team6.voca.service;

import com.team6.voca.common.exception.NotFoundException;
import com.team6.voca.domain.word.Word;
import com.team6.voca.dto.word.WordCreateRequest;
import com.team6.voca.dto.word.WordResponseDto;
import com.team6.voca.dto.word.WordUpdateRequest;
import com.team6.voca.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// [캡슐화/모듈화] 데이터베이스 트랜잭션 관리를 Service 계층으로 캡슐화하여, 
// Controller는 비즈니스 로직이 어떻게 DB에 반영되는지 몰라도 되게끔 분리합니다.
@Transactional(readOnly = true) 
public class WordService {

    // [다형성/정보은닉] JpaRepository 인터페이스를 주입받아 사용함으로써, 
    // 구체적인 데이터 접근 기술(구현체)이 변경되더라도 Service 로직은 영향을 받지 않습니다.
    private final WordRepository wordRepository;

    public List<WordResponseDto> getAllWords() {
        return wordRepository.findAll().stream()
                .map(WordResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public WordResponseDto createWord(WordCreateRequest request) {
        Word word = Word.builder()
                .englishWord(request.englishWord())
                .koreanMeaning(request.koreanMeaning())
                .partOfSpeech(request.partOfSpeech())
                .level(request.level())
                .build();
        return WordResponseDto.from(wordRepository.save(word));
    }

    @Transactional
    public WordResponseDto updateWord(Long id, WordUpdateRequest request) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("단어를 찾을 수 없습니다."));
        word.update(request.englishWord(), request.koreanMeaning(), request.partOfSpeech(), request.level());
        return WordResponseDto.from(word);
    }

    @Transactional
    public void deleteWord(Long id) {
        if (!wordRepository.existsById(id)) {
            throw new NotFoundException("단어를 찾을 수 없습니다.");
        }
        wordRepository.deleteById(id);
    }
}