package me.haneul.dto;

import me.haneul.entity.BoardLikes;
import me.haneul.entity.Comments;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class CommentResponseDTO {
    private Long id;
    private Long board_id;
    private String writer;
    private String contents;
    private LocalDateTime createAt;
    private LocalDateTime modifyAt;
    private Integer likes;
    private Integer dislikes;
    private Integer total;
    private Long parentid;
    private List<Comments> childcommentlist;

    public CommentResponseDTO(Comments comments) {  //Comments 엔티티를 받아서 CommentResponseDTO 객체로 만들기 위한 생성자
        this.id = comments.getId();
        this.board_id = comments.getBoardId();
        this.writer = comments.getWriter();
        this.contents = comments.getContents();
        this.createAt = comments.getCreateAt();
        this.modifyAt = comments.getModifyAt();
        if(comments.getLikes() == null) {
            this.likes = 0;
            this.dislikes = 0;
            this.total = 0;
        }
        else {
            this.likes = Collections.frequency(comments.getLikes().stream().map(BoardLikes::getLikes).toList(), true);  //true 갯수
            this.dislikes = Collections.frequency(comments.getLikes().stream().map(BoardLikes::getLikes).toList(), false);  //false 갯수
            this.total = likes - dislikes;
        }
        if(comments.getParentId() != null)
            this.parentid = comments.getParentId();
        if(comments.getChildComment() != null)
            this.childcommentlist = comments.getChildComment();
    }
}
