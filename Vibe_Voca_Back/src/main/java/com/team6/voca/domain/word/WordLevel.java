// src/main/java/com/team6/voca/domain/WordLevel.java
package com.team6.voca.domain.word;

// [캡슐화/모듈화] 난이도를 일반 문자열(String)이 아닌 Enum 상수로 제한하여, 
// 오타나 잘못된 값이 DB에 들어가는 것을 원천 차단하고 데이터 정합성을 보장합니다.
public enum WordLevel {
    NEWBIE,     // 초급
    BEGINNER,
    INTERMEDIATE, // 중급
    ADVANCED,      
    HIGHLEVEL // 고급
}