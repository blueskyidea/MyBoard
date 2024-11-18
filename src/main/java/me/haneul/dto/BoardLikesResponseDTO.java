package me.haneul.dto;

import me.haneul.entity.BoardLikes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardLikesResponseDTO {
    private Long board_id; //게시글 번호
    private Long comment_id; //댓글 번호
    private String title;  //게시글 제목
    private String contents;  //게시글 내용
    private String writer;  //게시글 작성자
    private Long id;
    private String member;  //좋아요, 싫어요 누른 아이디
    private Boolean likes;

    public BoardLikesResponseDTO(BoardLikes boardLikes) {  //BoardLikes 엔티티를 받아서 BoardLikesDTO 객체로 만들기 위한 생성자
        if(boardLikes.getComments() == null) {  //게시글에 대한 좋아요(게시글에 대한 정보 저장)
            this.board_id = boardLikes.getBoard().getId();
            this.title = boardLikes.getBoard().getTitle();
            this.contents = boardLikes.getBoard().getContents();
            this.writer = boardLikes.getBoard().getWriter();
        }
        else {  //댓글에 대한 좋아요(댓글에 대한 정보 저장)
            this.comment_id = boardLikes.getComments().getId();
            this.contents = boardLikes.getComments().getContents();
            this.writer = boardLikes.getComments().getWriter();
        }
        //좋아요, 싫어요 누른 사람에 대한 정보 저장
        this.id = boardLikes.getId();
        this.member = boardLikes.getWriter();
        this.likes = boardLikes.getLikes();
    }
}
