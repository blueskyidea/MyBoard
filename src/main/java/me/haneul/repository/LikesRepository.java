package me.haneul.repository;

import me.haneul.entity.BoardLikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<BoardLikes, Long> {
    Optional<BoardLikes> findByBoard_IdAndWriter(Long id, String writer);  //게시글 번호, 아이디로 조회
    Optional<BoardLikes> findByComments_IdAndWriter(Long id, String writer);  //게시글 번호, 아이디로 조회
}
