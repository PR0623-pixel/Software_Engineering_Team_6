package com.team6.voca.service;

import com.team6.voca.domain.quiz.ErrorNote;
import com.team6.voca.domain.quiz.QuizResult;
import com.team6.voca.domain.word.Word;
import com.team6.voca.dto.quiz.QuizGenerateResponseDto;
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

// 파일 경로: src/main/java/com/team6/voca/service/QuizService.java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizService {

    private static final int TIMER_QUESTION_BLOCK_SIZE = 5;
    private static final int TIMER_SECONDS_PER_BLOCK = 45;
    private static final int INITIAL_LEVEL_TEST_SECONDS = 300;

    private final WordRepository wordRepository;
    private final QuizResultRepository quizResultRepository;

    // [캡슐화] 문제 목록과 타이머 정책을 한 응답으로 묶어 제공합니다.
    public QuizGenerateResponseDto generateQuiz(int count, String level, String quizType) {
        List<QuizQuestionResponseDto> questions = generateQuestions(count, level);
        boolean fixedTimer = "level-test".equalsIgnoreCase(quizType)
                || "initial-level-test".equalsIgnoreCase(quizType);
        int timerSeconds = fixedTimer
                ? INITIAL_LEVEL_TEST_SECONDS
                : calculateQuizTimerSeconds(questions.size());

        return QuizGenerateResponseDto.of(questions, timerSeconds, fixedTimer);
    }

    // [모듈화] 일반 퀴즈 타이머 정책은 이 메서드만 수정하면 바뀌도록 분리합니다.
    private int calculateQuizTimerSeconds(int questionCount) {
        int safeQuestionCount = Math.max(questionCount, 1);
        int blockCount = (int) Math.ceil((double) safeQuestionCount / TIMER_QUESTION_BLOCK_SIZE);
        return blockCount * TIMER_SECONDS_PER_BLOCK;
    }

    // [캡슐화] 퀴즈를 어떻게 생성하고(Random) 문제를 어떻게 구성하는지에 대한
    // 모든 복잡한 비즈니스 로직을 이 메서드 하나로 캡슐화하여 컨트롤러에 제공합니다.
    public List<QuizQuestionResponseDto> generateQuestions(int count, String level) {
        List<Word> targetWords = wordRepository.findRandomWords(PageRequest.of(0, count));

        return targetWords.stream()
                .map(word -> QuizQuestionResponseDto.of(word.getId(), word.getKoreanMeaning(), word.getEnglishWord()))
                .collect(Collectors.toList());
    }

    // [캡슐화] 채점, 점수 계산, 오답노트 생성이라는 복잡한 상태 변경 로직을 하나의 트랜잭션으로 묶어 캡슐화합니다.
    @Transactional
    public void gradeAndSaveQuiz(QuizSubmitRequestDto request) {
        int score = 0;
        List<QuizSubmitRequestDto.QuizAnswerDto> answers =
                request.answers() == null ? List.of() : request.answers();
        int totalQuestions = request.totalQuestionCount() == null
                ? answers.size()
                : request.totalQuestionCount();
        List<ErrorNote> errorNotes = new ArrayList<>();

        // [모듈화] 유저가 제출한 모든 답안을 순회하며 DB의 실제 영어 단어와 대조합니다.
        for (QuizSubmitRequestDto.QuizAnswerDto answer : answers) {
            Word word = wordRepository.findById(answer.wordId())
                    .orElseThrow(() -> new IllegalArgumentException("단어를 찾을 수 없습니다."));

            String submittedWord = answer.submittedWord() == null ? "" : answer.submittedWord().trim();

            if (word.getEnglishWord().equalsIgnoreCase(submittedWord)) {
                score++;
            } else {
                ErrorNote errorNote = ErrorNote.builder()
                        .word(word)
                        .submittedAnswer(submittedWord)
                        .build();
                errorNotes.add(errorNote);
            }
        }

        QuizResult quizResult = QuizResult.builder()
                .userId(request.userId())
                .score(score)
                .totalQuestions(totalQuestions)
                .build();

        // [캡슐화] 연관관계 편의 메서드를 통해 ErrorNote가 QuizResult를 알도록 연결합니다.
        errorNotes.forEach(quizResult::addErrorNote);

        quizResultRepository.save(quizResult);
    }
}
