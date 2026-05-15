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
    
    // [다형성] JpaRepository 인터페이스를 상속받아, 데이터베이스 접근에 필요한 다양한 구현체를 다형성 있게 활용합니다

    // (기존에 작성되어 있던 다른 메서드들은 그대로 유지합니다)

    /**
     * [모듈화/정보은닉] 정답 단어를 제외한 나머지 단어들 중 지정된 개수(limit)만큼 무작위로 추출합니다.
     * Service 계층에서는 복잡한 Random 추출 쿼리를 알 필요 없이 이 메서드만 호출하도록 정보은닉을 달성합니다.
     * * 주의: DB 테이블명이 'word'인지 'words'인지에 따라 FROM 절의 이름을 맞춰주세요. (기본값: word)
     */
    @Query(value = "SELECT * FROM word WHERE id != :wordId ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Word> findRandomWordsNotMatching(@Param("wordId") Long wordId, @Param("limit") int limit);
}
