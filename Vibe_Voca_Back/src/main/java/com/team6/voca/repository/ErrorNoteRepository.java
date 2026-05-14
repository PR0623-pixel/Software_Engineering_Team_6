// src/main/java/com/team6/voca/repository/ErrorNoteRepository.java
package com.team6.voca.repository;

import com.team6.voca.domain.quiz.ErrorNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ErrorNoteRepository extends JpaRepository<ErrorNote, Long> {
    
    // [모듈화] 특정 사용자의 오답노트만 조회하여 데이터 접근 범위를 제한함
    @Query("SELECT en FROM ErrorNote en JOIN en.quizResult qr WHERE qr.user.id = :userId")
    List<ErrorNote> findAllByUserId(@Param("userId") Long userId);
}