package me.haneul.dto;

import me.haneul.entity.Board;
import me.haneul.entity.BoardLikes;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

// 게시글 정보를 리턴할 응답(Response) 클래스
@Getter
@Setter
public class BoardResponseDTO {
    private Long id;
    private String title;
    private String writer;
    private String contents;
    private LocalDateTime createAt;
    private LocalDateTime modifyAt;
    private Integer likes;
    private Integer dislikes;
    private Integer total;
    private List<CommentResponseDTO> commentsList;

    public BoardResponseDTO(Board board) {  //Board 엔티티를 받아서 BoardDTO 객체로 만들기 위한 생성자
        this.id = board.getId();
        this.title = board.getTitle();
        this.writer = board.getWriter();
        this.contents = board.getContents();
        this.createAt = board.getCreateAt();
        this.modifyAt = board.getModifyAt();
        if(board.getLikes() == null) {
            this.likes = 0;
            this.dislikes = 0;
            this.total = 0;
        }
        else {
            this.likes = Collections.frequency(board.getLikes().stream().map(BoardLikes::getLikes).toList(), true);  //true 갯수
            this.dislikes = Collections.frequency(board.getLikes().stream().map(BoardLikes::getLikes).toList(), false);  //false 갯수
            this.total = likes - dislikes;
        }
    }
}
