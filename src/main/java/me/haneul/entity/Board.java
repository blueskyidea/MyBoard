package me.haneul.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.haneul.dto.InsertDTO;

import java.util.List;

@Entity  //DB에 저장할 엔티티
@Getter
@NoArgsConstructor
@Table(name = "Board")
public class Board extends Post{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;  //게시글 번호

    @Column(nullable = false)
    private String title;  //제목

    //columnDefinition = "TEXT" -> 긴 글을 쓸 수 있도록 추가해줌
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String contents;  //내용

    @Column(nullable = false)
    private String writer;  //작성자

    /*@OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @OrderBy("id asc") //댓글 정렬
    private List<Comments> comments;*/

    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<BoardLikes> likes;

    @Builder
    public Board(String title, String contents, String writer) {  //DB에 저장
        this.title = title;
        this.contents = contents;
        this.writer = writer;
    }

    public void revise(InsertDTO insertDTO) {
        this.title = insertDTO.getTitle();
        this.contents = insertDTO.getContents();
    }
}
