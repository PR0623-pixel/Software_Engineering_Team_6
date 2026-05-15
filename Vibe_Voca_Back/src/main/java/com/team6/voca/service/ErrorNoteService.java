// src/main/java/com/team6/voca/service/ErrorNoteService.java
package com.team6.voca.service;

import com.team6.voca.common.exception.NotFoundException;
import com.team6.voca.domain.quiz.ErrorNote;
import com.team6.voca.domain.word.Word;
import com.team6.voca.domain.word.WordExample;
import com.team6.voca.dto.ErrorNote.ErrorNoteListResponseDto;
import com.team6.voca.dto.ErrorNote.ErrorNoteQuizResponseDto;
import com.team6.voca.repository.ErrorNoteRepository;
import com.team6.voca.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// [모듈화] 클래스 레벨에 readOnly 트랜잭션을 적용하여 전체적인 조회 성능을 최적화하고 데이터 무결성을 보장합니다.
@Transactional(readOnly = true)
public class ErrorNoteService {

    private final ErrorNoteRepository errorNoteRepository;
    private final WordRepository wordRepository;

    /**
     * 사용자의 전체 오답노트 목록 조회
     */
    public List<ErrorNoteListResponseDto> getErrorNotes(Long userId) {
        List<ErrorNote> errorNotes = errorNoteRepository.findAllByUserId(userId);
        
        // [다형성/캡슐화] Stream API와 DTO 내부의 from 메서드를 활용하여 안전하게 데이터를 매핑합니다.
        return errorNotes.stream()
                .map(ErrorNoteListResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * [캡슐화] 오답노트 메모 수정 (Update)
     * 데이터의 상태 변경은 반드시 도메인 객체 내부의 비즈니스 로직(updateMemo)을 통하도록 강제합니다.
     */
    @Transactional // 쓰기 작업이므로 readOnly 오버라이딩
    public void updateErrorNoteMemo(Long id, String memo) {
        ErrorNote errorNote = errorNoteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("오답 정보를 찾을 수 없습니다."));
        errorNote.updateMemo(memo);
    }

    /**
     * 오답노트 삭제 (Delete - 암기 완료 처리 등)
     */
    @Transactional
    public void deleteErrorNote(Long id) {
        if (!errorNoteRepository.existsById(id)) {
            throw new NotFoundException("삭제할 오답노트가 존재하지 않습니다.");
        }
        errorNoteRepository.deleteById(id);
    }

    /**
     * [모듈화] 오답노트 기반 사지선다형 재테스트 문제 생성
     * 외부 API(URL)를 사용하지 않고, 오로지 기존 DB의 단어와 예문 데이터를 활용하여 생성합니다.
     */
    public List<ErrorNoteQuizResponseDto> generateQuizFromErrors(Long userId, int count) {
        List<ErrorNote> userErrors = errorNoteRepository.findAllByUserId(userId);
        
        // 문제 출제 순서를 무작위로 섞음
        Collections.shuffle(userErrors);

        return userErrors.stream()
                .limit(count)
                .map(error -> {
                    Word word = error.getWord();
                    
                    // 1. [정보은닉] 사지선다 보기 리스트 생성 (구체적 생성 방식은 내부 메서드로 숨김)
                    List<String> options = createMultipleChoices(word);

                    // 2. DB에 저장된 예문(WordExample) 추출
                    String sentence = "";
                    if (word.getExamples() != null && !word.getExamples().isEmpty()) {
                        // 기본적으로 첫 번째 예문을 가져옴
                        WordExample example = word.getExamples().get(0);
                        sentence = example.getExampleSentence();
                    }

                    // 3. [모듈화] Record 객체의 정적 팩토리 메서드를 활용하여 불변 DTO 생성
                    return ErrorNoteQuizResponseDto.of(
                            word.getId(),
                            word.getEnglishWord(),
                            word.getKoreanMeaning(),
                            options,
                            sentence,
                            error.getMemo()
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * [캡슐화] 사지선다 보기 생성 (정답 1개 + 무작위 오답 3개)
     * 이 메서드는 외부(Controller 등)에 노출되지 않으며 오로지 서비스 내부에서만 사용됩니다.
     */
// [캡슐화/정보은닉] 사지선다 보기를 생성하는 복잡한 과정은 private 메서드로 숨겨 외부(Controller 등)에서 알지 못하게 합니다.
    private List<String> createMultipleChoices(Word word) {
        List<String> options = new ArrayList<>();
        
        // 1. 실제 정답의 뜻을 보기에 추가합니다.
        options.add(word.getKoreanMeaning());
        
        // 2. 정답을 제외한 무작위 오답 3개 추출
        // [수정 사항] wordid를 word.getId()로, limit를 3으로 직접 지정하여 변수 미정의 오류를 해결합니다.
        List<Word> distractors = wordRepository.findRandomWordsNotMatching(
                word.getId(), 
                PageRequest.of(0, 3)
        );
        
        // 3. 추출된 오답 단어들의 뜻을 options 리스트에 추가합니다.
        for (Word distractor : distractors) {
            options.add(distractor.getKoreanMeaning());
        }
        
        // 4. (선택적 예외 처리) DB에 저장된 전체 단어가 4개 미만일 경우를 대비한 더미 데이터 삽입 방어 로직
        while (options.size() < 4) {
            options.add("임시 오답 " + options.size());
        }
        
        // 5. 정답이 항상 첫 번째에 위치하지 않도록 Java 내장 기능을 활용해 보기를 무작위로 섞습니다.
        Collections.shuffle(options);
        
        return options;
    }
}