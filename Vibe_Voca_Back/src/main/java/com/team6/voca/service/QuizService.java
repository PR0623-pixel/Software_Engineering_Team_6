package com.team6.voca.service;

import com.team6.voca.domain.user.*;
import com.team6.voca.common.exception.NotFoundException;
import com.team6.voca.domain.quiz.ErrorNote;
import com.team6.voca.domain.quiz.QuizResult;
import com.team6.voca.domain.word.Word;
import com.team6.voca.dto.quiz.QuizQuestionResponseDto;
import com.team6.voca.dto.quiz.QuizSubmitRequestDto;
import com.team6.voca.repository.QuizResultRepository;
import com.team6.voca.repository.UserRepository;
import com.team6.voca.repository.WordRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizService {

    private final WordRepository wordRepository;
    private final QuizResultRepository quizResultRepository;
    private final UserRepository userRepository;

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

    /**
     * [모듈화] 퀴즈 채점 및 결과 저장 로직
     * 사용자가 제출한 답안을 검증하고, 틀린 단어는 자동으로 오답노트에 기록합니다.
     */
    @Transactional
    public void gradeAndSaveQuiz(QuizSubmitRequestDto request) {
        // 1. 사용자 조회
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        // 2. [캡슐화] QuizResult 객체를 생성하여 채점 데이터를 관리합니다.
        QuizResult result = QuizResult.builder()
                .user(user)
                .totalQuestions(request.answers().size())
                .build();

        int correctCount = 0;

        // 3. 제출된 답안 리스트를 순회하며 채점 진행
        for (var answerDto : request.answers()) {
            Word word = wordRepository.findById(answerDto.wordId())
                    .orElseThrow(() -> new NotFoundException("단어 정보를 찾을 수 없습니다."));

            // [정보은닉] 단어의 정답과 사용자의 입력값을 비교합니다.
            boolean isCorrect = word.getEnglishWord().equalsIgnoreCase(answerDto.submittedAnswer());

            if (isCorrect) {
                correctCount++;
            } else {
                // 4. [자동 저장] 틀린 경우 ErrorNote 엔티티를 생성하여 결과 객체에 추가합니다.
                ErrorNote errorNote = ErrorNote.builder()
                        .word(word)
                        .submittedAnswer(answerDto.submittedAnswer())
                        .memo("") // 초기 메모는 빈 값으로 설정
                        .build();
                
                // [캡슐화] QuizResult 내부의 addErrorNote 메서드를 통해 연관관계를 편의적으로 설정합니다.
                result.addErrorNote(errorNote);
            }
        }

        // 5. 최종 점수 계산 및 결과 업데이트
        // [캡슐화] 객체 내부의 데이터를 직접 수정하지 않고 메서드를 통해 업데이트합니다.
        double score = ((double) correctCount / result.getTotalQuestions()) * 100;
        result.updateResult(correctCount, (int) score);

        // 6. DB 저장 (CascadeType.ALL 설정 시 ErrorNote도 함께 저장됩니다)
        quizResultRepository.save(result);
    }
}