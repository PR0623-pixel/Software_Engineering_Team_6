// src/main/java/com/team6/voca/service/ErrorNoteService.java
package com.team6.voca.service;

import com.team6.voca.common.exception.NotFoundException;
import com.team6.voca.domain.quiz.ErrorNote;
import com.team6.voca.domain.word.Word;
import com.team6.voca.domain.word.WordExample;
import com.team6.voca.dto.ErrorNote.ErrorNoteQuizResponseDto;
import com.team6.voca.repository.ErrorNoteRepository;
import com.team6.voca.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<ErrorNote> getErrorNotes(Long userId) {
        return errorNoteRepository.findAllByUserId(userId);
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
    private List<String> createMultipleChoices(Word correctWord) {
        List<String> choices = new ArrayList<>();
        
        // 정답 뜻 추가
        choices.add(correctWord.getKoreanMeaning());

        // [다형성 활용] WordRepository의 기능을 재사용하여 현재 단어가 아닌 무작위 단어 3개를 추출
        List<Word> distractors = wordRepository.findRandomWordsNotMatching(correctWord.getId(), 3);
        distractors.forEach(d -> choices.add(d.getKoreanMeaning()));

        // 보기 개수가 4개가 안 될 경우를 대비한 방어 로직 (데이터가 적은 초기 개발 단계용)
        int dummyCount = 1;
        while (choices.size() < 4) {
            choices.add("임시 보기 " + dummyCount++);
        }

        // 보기의 순서를 무작위로 섞어 항상 1번이 정답이 되지 않게 함
        Collections.shuffle(choices);
        
        return choices;
    }
}