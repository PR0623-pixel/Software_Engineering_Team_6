package com.team6.voca.repository;

import com.team6.voca.domain.word.Word;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    // [다형성/캡슐화] 쿼리문에서 LIMIT 절을 제거하고, Pageable 객체를 파라미터로 받아
    // 데이터베이스 방언(Dialect)에 맞는 LIMIT 처리를 Spring Data JPA 프레임워크에 위임합니다.
    
    // 1. 전체 단어 중 N개를 랜덤 추출 (LIMIT 제거)
    @Query(value = "SELECT * FROM words ORDER BY RAND()", nativeQuery = true)
    List<Word> findRandomWords(Pageable pageable);

    // 2. 특정 단어를 제외한 무작위 단어 뜻 N개 추출 (LIMIT 제거)
    @Query(value = "SELECT korean_meaning FROM words WHERE id != :excludeWordId ORDER BY RAND()", nativeQuery = true)
    List<String> findRandomMeaningsExcluding(@Param("excludeWordId") Long excludeWordId, Pageable pageable);

    // 3. 특정 레벨의 단어를 무작위로 N개 추출
    @Query(value = "SELECT * FROM words WHERE level = :level ORDER BY RAND()", nativeQuery = true)
    List<Word> findRandomWordsByLevel(@Param("level") String level, Pageable pageable);
}