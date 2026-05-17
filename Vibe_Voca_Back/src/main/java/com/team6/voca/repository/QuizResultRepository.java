// src/main/java/com/team6/voca/repository/QuizResultRepository.java
package com.team6.voca.repository;

import com.team6.voca.domain.quiz.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    @Query("SELECT COUNT(qr) FROM QuizResult qr WHERE qr.user.id = :userId AND qr.createdAt >= :startOfDay")
    long countTodayQuizzesByUser(@Param("userId") Long userId, @Param("startOfDay") LocalDateTime startOfDay);
}