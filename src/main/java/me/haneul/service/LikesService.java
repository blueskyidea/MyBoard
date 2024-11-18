package me.haneul.service;

import me.haneul.dto.BoardLikesResponseDTO;
import me.haneul.entity.Board;
import me.haneul.entity.BoardLikes;
import me.haneul.entity.Comments;
import me.haneul.entity.Member;
import me.haneul.repository.BoardRepository;
import me.haneul.repository.CommentRepository;
import me.haneul.repository.LikesRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LikesService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;

    public LikesService(BoardRepository boardRepository, CommentRepository commentRepository, LikesRepository likesRepository) {
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
        this.likesRepository = likesRepository;
    }

    //게시글 좋아요, 싫어요 저장
    @Transactional
    public BoardLikesResponseDTO likes(Long id, Boolean like) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        //사용자가 기존에 좋아요 or 싫어요를 누른적이 있는지 확인
        Optional<BoardLikes> boardLikes = likesRepository.findByBoard_IdAndWriter(id, name);
        boardLikes.ifPresent(boardlikes -> {  //null이 아니면(기존에 좋아요 혹은 싫어요를 눌렀으면)
            if(boardlikes.getLikes() == like) {  //똑같은거 누르면
                likesRepository.delete(boardlikes);  //삭제
            }
            else {  //다른거 누르면
                boardlikes.revise(like);  //수정
            }
        });

        //boardLikes가 null이면 실행(기존에 좋아요 or 싫어요를 누르지 않았으면)
        BoardLikes boardlikes = boardLikes.orElseGet(() -> {
            return likesRepository.save(BoardLikes.of(like, board, name));  //저장
        });

        return new BoardLikesResponseDTO(boardlikes);
    }

    //댓글 좋아요, 싫어요 저장
    @Transactional
    public void commentlikes(Long id, Boolean like) throws Exception {
        Comments comments = commentRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("댓글이 존재하지 않습니다.")
        );

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        //사용자가 기존에 좋아요 or 싫어요를 누른적이 있는지 확인
        Optional<BoardLikes> boardLikes = likesRepository.findByComments_IdAndWriter(id, name);
        boardLikes.ifPresent(likes -> {  //null이 아니면(기존에 좋아요 혹은 싫어요를 눌렀으면)
            if(likes.getLikes() == like) {  //똑같은거 누르면
                likesRepository.delete(likes);  //삭제
            }
            else {  //다른거 누르면
                likes.revise(like);  //수정
            }
        });

        //boardLikes가 null이면 실행(기존에 좋아요 or 싫어요를 누르지 않았으면)
        BoardLikes boardlikes = boardLikes.orElseGet(() -> {
            return likesRepository.save(BoardLikes.of(like, comments, name));  //저장
        });
    }
}
