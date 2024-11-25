package me.haneul.repository;

import me.haneul.dto.BoardWithNicknameDTO;
import me.haneul.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
