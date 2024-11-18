package me.haneul.entity;

import me.haneul.dto.CommentDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Comments")
//댓글은 좋아요, 싫어요 취소, 수정, 확인 불가능
public class Comments extends Post {
    /*@ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;  //게시글 번호
    */
    @Column(name = "board_id", nullable = false)
    private Long boardId;  //게시글 번호

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;  //댓글 번호

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String contents;  //댓글 내용

    @Column(nullable = false)
    private String writer;  //작성자

    @OneToMany(mappedBy = "comments", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<BoardLikes> likes;

    @Column
    private Long parentId;  //부모댓글 번호(대댓글)

    @OneToMany(mappedBy = "parentId")
    private List<Comments> childComment = new ArrayList<>();  //자식 댓글들(대댓글)

    @Builder
    public Comments(CommentDTO commentDTO, Board board, String writer) {  //DTO를 받아서 DB에 저장
        this.contents = commentDTO.getContents();
        this.boardId = board.getId();
        this.writer = writer;
        this.parentId = commentDTO.getParentid();
    }

    public void revise(String contents) {
        this.contents = contents;
    }

    public static Comments of(CommentDTO commentDTO, Board board, String writer) {
        return Comments.builder()
                .commentDTO(commentDTO)
                .board(board)
                .writer(writer)
                .build();
    }

    public void setChildComment(Comments child) {
        this.getChildComment().add(child);
    }
}
