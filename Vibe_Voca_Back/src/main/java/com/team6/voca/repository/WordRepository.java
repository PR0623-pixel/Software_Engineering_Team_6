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
    
    // [다형성/모듈화] Spring Data JPA의 Pageable 인터페이스를 활용하여 LIMIT 처리를 프레임워크에 위임합니다.
    // 외부 비즈니스 로직은 DB 쿼리의 페이징 방식이 어떻게 동작하는지 몰라도 Pageable 객체만 넘겨주면 무작위 단어를 얻을 수 있습니다.
    @Query(value = "SELECT * FROM words WHERE id != :wordId ORDER BY RAND()", nativeQuery = true)
    List<Word> findRandomWordsNotMatching(@Param("wordId") Long wordId, Pageable pageable);
}

