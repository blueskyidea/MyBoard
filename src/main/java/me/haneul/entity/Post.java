package me.haneul.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass  //공통으로 사용하는 맵핑 정보만 상속하는 부모 클래스에 선언
@EntityListeners(AuditingEntityListener.class)
public class Post {
    @CreatedDate  //엔티티가 생성되어 저장될때 시간을 자동 저장
    private LocalDateTime createAt;

    @LastModifiedDate  //엔티티가 수정된 시간을 자동 저장
    private LocalDateTime modifyAt;
}
