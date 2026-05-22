package com.team6.voca.service;

import com.team6.voca.domain.user.*;
import com.team6.voca.common.exception.NotFoundException;
import com.team6.voca.domain.quiz.ErrorNote;
import com.team6.voca.domain.quiz.QuizResult;
import com.team6.voca.domain.word.Word;
import com.team6.voca.domain.word.WordLevel;
import com.team6.voca.dto.quiz.QuizGenerateResponseDto;
import com.team6.voca.dto.quiz.QuizQuestionResponseDto;
import com.team6.voca.dto.quiz.QuizSubmitRequestDto;
import com.team6.voca.repository.QuizResultRepository;
import com.team6.voca.repository.UserRepository;
import com.team6.voca.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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
    private final UserRepository userRepository;
    private final PointService pointService;

    public QuizGenerateResponseDto generateQuiz(int count, String level, String quizType) {
        List<QuizQuestionResponseDto> questions = generateQuestions(count, level);
        boolean fixedTimer = "level-test".equalsIgnoreCase(quizType)
                || "initial-level-test".equalsIgnoreCase(quizType);
        int timerSeconds = fixedTimer
                ? INITIAL_LEVEL_TEST_SECONDS
                : calculateQuizTimerSeconds(questions.size());

        return QuizGenerateResponseDto.of(questions, timerSeconds, fixedTimer);
    }

    private int calculateQuizTimerSeconds(int questionCount) {
        int safeQuestionCount = Math.max(questionCount, 1);
        int blockCount = (int) Math.ceil((double) safeQuestionCount / TIMER_QUESTION_BLOCK_SIZE);
        return blockCount * TIMER_SECONDS_PER_BLOCK;
    }

    public List<QuizQuestionResponseDto> generateQuestions(int count, String level) {
        List<Word> targetWords = wordRepository.findRandomWords(PageRequest.of(0, count));

        return targetWords.stream()
                .map(word -> QuizQuestionResponseDto.of(word.getId(), word.getKoreanMeaning(), word.getEnglishWord()))
                .collect(Collectors.toList());
    }

    public List<QuizQuestionResponseDto> generateLevelTestQuestions() {
        List<QuizQuestionResponseDto> questions = new ArrayList<>();
        for (WordLevel level : WordLevel.values()) {
            List<Word> words = wordRepository.findRandomWordsByLevel(level.name(), PageRequest.of(0, 3));
            words.stream()
                    .map(w -> QuizQuestionResponseDto.of(w.getId(), w.getKoreanMeaning(), w.getEnglishWord()))
                    .forEach(questions::add);
        }
        Collections.shuffle(questions);
        return questions;
    }

    @Transactional
    public void gradeAndSaveQuiz(QuizSubmitRequestDto request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        List<QuizSubmitRequestDto.QuizAnswerDto> answers =
                request.answers() == null ? List.of() : request.answers();
        int totalQuestions = request.totalQuestionCount() == null
                ? answers.size()
                : request.totalQuestionCount();

        QuizResult result = QuizResult.builder()
                .user(user)
                .totalQuestions(totalQuestions)
                .build();

        int correctCount = 0;

        for (QuizSubmitRequestDto.QuizAnswerDto answer : answers) {
            Word word = wordRepository.findById(answer.wordId())
                    .orElseThrow(() -> new NotFoundException("단어 정보를 찾을 수 없습니다."));

            String submitted = answer.submittedAnswer() == null ? "" : answer.submittedAnswer().trim();
            boolean isCorrect = word.getEnglishWord().equalsIgnoreCase(submitted);

            if (isCorrect) {
                correctCount++;
            } else {
                ErrorNote errorNote = ErrorNote.builder()
                        .word(word)
                        .submittedAnswer(submitted)
                        .memo("")
                        .build();
                result.addErrorNote(errorNote);
            }
        }

        double score = totalQuestions > 0 ? ((double) correctCount / totalQuestions) * 100 : 0;
        result.updateResult(correctCount, (int) score);
        quizResultRepository.save(result);

        pointService.awardQuizPoints(request.userId());
    }
}
