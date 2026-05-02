// src/main/java/com/team6/voca/domain/BaseTimeEntity.java
package com.team6.voca.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

// [상속] 모든 도메인 엔티티(Word, QuizResult 등)가 이 클래스를 상속받아 
// DB 생성일자/수정일자 컬럼 코드를 중복 작성하지 않도록 합니다.
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}