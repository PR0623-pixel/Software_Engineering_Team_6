package com.team6.voca.service;

import com.team6.voca.domain.quiz.ErrorNote;
import com.team6.voca.domain.quiz.QuizResult;
import com.team6.voca.domain.word.Word;
import com.team6.voca.dto.quiz.QuizQuestionResponseDto;
import com.team6.voca.dto.quiz.QuizSubmitRequestDto;
import com.team6.voca.repository.QuizResultRepository;
import com.team6.voca.repository.WordRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizService {

    private final WordRepository wordRepository;

    private final QuizResultRepository quizResultRepository;

    // [캡슐화] 퀴즈를 어떻게 생성하고(Random) 문제를 어떻게 구성하는지에 대한
    // 모든 복잡한 비즈니스 로직을 이 메서드 하나로 캡슐화하여 컨트롤러에 제공합니다.
    public List<QuizQuestionResponseDto> generateQuestions(int count, String level) {

        // 1. 출제할 단어 N개를 무작위로 가져옵니다.
        List<Word> targetWords = wordRepository.findRandomWords(PageRequest.of(0, count));

        // 2. 한국어 뜻을 문제로, 영어 단어를 정답으로 DTO를 조립합니다.
        return targetWords.stream()
                .map(word -> QuizQuestionResponseDto.of(word.getId(), word.getKoreanMeaning(), word.getEnglishWord()))
                .collect(Collectors.toList());
    }

    // [캡슐화] 채점, 점수 계산, 오답노트 생성이라는 복잡한 상태 변경 로직을 하나의 트랜잭션으로 묶어 캡슐화합니다.
    @Transactional
    public void gradeAndSaveQuiz(QuizSubmitRequestDto request) {
        int score = 0;
        int totalQuestions = request.answers().size();
        List<ErrorNote> errorNotes = new ArrayList<>();

        // 1. [모듈화] 유저가 제출한 모든 답안을 순회하며 DB의 실제 영어 단어와 대조(채점)합니다.
        for (QuizSubmitRequestDto.QuizAnswerDto answer : request.answers()) {
            Word word = wordRepository.findById(answer.wordId())
                    .orElseThrow(() -> new IllegalArgumentException("단어를 찾을 수 없습니다."));

            if (word.getEnglishWord().equalsIgnoreCase(answer.submittedWord())) {
                score++; // 정답일 경우 점수 증가
            } else {
                // [다형성/캡슐화] 오답일 경우, JPA 연관관계를 활용하여 저장할 ErrorNote 객체를 조립합니다.
                // (QuizResult가 아직 없으므로 임시로 빈 QuizResult를 참조하게 하거나 나중에 매핑합니다)
                ErrorNote errorNote = ErrorNote.builder()
                        .word(word)
                        .submittedAnswer(answer.submittedWord())
                        .build();
                errorNotes.add(errorNote);
            }
        }

        // 2. 최종 결과를 바탕으로 QuizResult 세션을 생성합니다.
        QuizResult quizResult = QuizResult.builder()
                .userId(request.userId())
                .score(score)
                .totalQuestions(totalQuestions)
                .build();

        // 3. [상속/캡슐화] 양방향 연관관계 편의 메서드(QuizResult 내부 구현 필요)를 통해 오답노트를 세션에 귀속시킵니다.
        // cascade = CascadeType.ALL 속성에 의해 QuizResult 하나만 save() 해도 오답노트 N개가 자동으로 DB에 insert 됩니다.
        errorNotes.forEach(quizResult.getErrorNotes()::add);

        quizResultRepository.save(quizResult);
    }
}
