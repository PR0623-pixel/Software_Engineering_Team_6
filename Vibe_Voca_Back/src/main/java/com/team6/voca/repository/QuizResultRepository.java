// src/main/java/com/team6/voca/repository/QuizResultRepository.java
package com.team6.voca.repository;

import com.team6.voca.domain.quiz.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// [모듈화] 퀴즈 결과(QuizResult)와 관련된 데이터베이스 접근 책임만을 전담하는 모듈로 분리합니다.
// [다형성] JpaRepository를 상속받아, 구현체 없이도 Spring Data JPA 프레임워크가 런타임에 데이터 접근 로직을 동적으로 제공하도록 설계합니다.
@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
}