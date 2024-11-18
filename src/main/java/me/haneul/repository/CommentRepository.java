package me.haneul.repository;

import me.haneul.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comments, Long> {
    List<Comments> findByWriterOrderByIdDesc(String writer); //id(게시글 번호) 기준 내림차순으로 조회
    Optional<Comments> findByBoardIdAndId(Long boardId, Long Id);  //게시판 번호, 댓글 번호로 조회
    List<Comments> findByParentIdIsNull();  //댓글 조회(대댓글 제외)
    List<Comments> findByBoardIdAndParentIdIsNull(Long Id);  //특정 게시글 댓글 조회(대댓글 제외)
    List<Comments> findByWriter(String id);  //아이디(작성자)로 조회
}
