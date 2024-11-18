package me.haneul.dto;

import me.haneul.entity.Board;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BoardDTO {
    private Long id;
    private String title;
    private String writer;
    private String contents;
    private LocalDateTime createAt;
    private LocalDateTime modifyAt;

    public BoardDTO(Board board) {  //Board 엔티티를 받아서 BoardDTO 객체로 만들기 위한 생성자
        this.id = board.getId();
        this.title = board.getTitle();
        this.writer = board.getWriter();
        this.contents = board.getContents();
        this.createAt = board.getCreateAt();
        this.modifyAt = board.getModifyAt();
    }
}
