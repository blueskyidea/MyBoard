package me.haneul.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity  //DB에 저장할 엔티티
@Getter
@NoArgsConstructor
@Table(name = "BoardLikes")
public class BoardLikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String writer;  //작성자(닉네임)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;  //게시글 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comments comments;  //댓글 번호

    @Column
    private Boolean likes;  //추천, 비추천

    @Builder
    public BoardLikes(Boolean likes, Board board, Comments comments, String writer) {  //DTO를 받아서 DB에 저장
        this.likes = likes;
        this.board = board;
        this.comments = comments;
        this.writer = writer;
    }

    //게시글 좋아요
    public static BoardLikes of(Boolean likes, Board board, String writer) {
        return BoardLikes.builder()
                .likes(likes)
                .board(board)
                .writer(writer)
                .build();
    }

    //댓글 좋아요
    public static BoardLikes of(Boolean likes, Comments comments, String writer) {
        return BoardLikes.builder()
                .likes(likes)
                .comments(comments)
                .writer(writer)
                .build();
    }

    public void revise(Boolean likes) {
        this.likes = likes;
    }
}
