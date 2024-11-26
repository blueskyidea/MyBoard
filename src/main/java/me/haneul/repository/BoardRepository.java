package me.haneul.repository;

import me.haneul.dto.BoardWithNicknameDTO;
import me.haneul.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByWriterOrderByIdDesc(String writer); //id(게시글 번호) 기준 내림차순으로 조회
    Optional<Board> findById(Long id);  //게시글 번호로 조회

    List<Board> findByWriter(String id);  //아이디(작성자)로 조회

    @Query("SELECT b, m.nickname " +
            "FROM Board b " +
            "JOIN Member m ON b.writer = m.id")
    List<Object[]> findAllWithNickname();  //게시판 정보와 member의 닉네임 정보 불러오기

    //제목+내용으로 검색
    @Query("SELECT b, m.nickname " +
            "FROM Board b " +
            "JOIN Member m ON b.writer = m.id " +
            "WHERE b.title LIKE %:keyword% OR b.contents LIKE %:keyword%")
    List<Object[]> findByTitleOrContentsContaining(@Param("keyword") String keyword);

    //제목으로 검색
    @Query("SELECT b, m.nickname " +
            "FROM Board b " +
            "JOIN Member m ON b.writer = m.id " +
            "WHERE b.title LIKE %:title%")
    List<Object[]> findByTitleContaining(@Param("title") String title);

    //내용으로 검색
    @Query("SELECT b, m.nickname " +
            "FROM Board b " +
            "JOIN Member m ON b.writer = m.id " +
            "WHERE b.contents LIKE %:contents%")
    List<Object[]> findByContentsContaining(@Param("contents") String contents);

    //닉네임으로 검색
    @Query("SELECT b, m.nickname " +
            "FROM Board b " +
            "JOIN Member m ON b.writer = m.id " +
            "WHERE m.nickname LIKE %:nickname%")
    List<Object[]> findByNicknameContaining(@Param("nickname") String nickname);
}
