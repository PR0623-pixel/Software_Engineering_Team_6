package com.team6.voca.service;

import com.team6.voca.common.exception.NotFoundException;
import com.team6.voca.domain.shop.PointPolicy;
import com.team6.voca.domain.user.User;
import com.team6.voca.repository.PointPolicyRepository;
import com.team6.voca.repository.QuizResultRepository;
import com.team6.voca.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;
    private final PointPolicyRepository pointPolicyRepository;
    private final QuizResultRepository quizResultRepository;

    @Transactional
    public void awardQuizPoints(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        int base = getPolicyPoints("QUIZ_COMPLETE");

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        long todayCount = quizResultRepository.countTodayQuizzesByUser(userId, startOfDay);

        int bonus = (todayCount == 1) ? getPolicyPoints("DAILY_BONUS") : 0;

        user.addPoints(base + bonus);
    }

    private int getPolicyPoints(String key) {
        return pointPolicyRepository.findByPolicyKey(key)
                .map(PointPolicy::getPoints)
                .orElse(0);
    }
}
