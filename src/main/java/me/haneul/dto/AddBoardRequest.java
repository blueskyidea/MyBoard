package me.haneul.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.haneul.entity.Board;

@NoArgsConstructor  //기본 생성자 추가
@AllArgsConstructor  //모든 필드 값을 파라미터로 받는 생성자 추가
@Getter
public class AddBoardRequest {
    //값 검증 애너테이션 추가
    @NotNull
    @Size(min = 1, max = 10)
    private String title;

    @NotNull
    private String contents;

    public Board toEntity(String writer) {  //생성자를 사용해 객체 생성
        return Board.builder()
                .title(title)
                .contents(contents)
                .writer(writer)
                .build();
    }
}
