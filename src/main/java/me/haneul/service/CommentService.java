package me.haneul.service;

import jakarta.servlet.http.HttpServletRequest;
import me.haneul.config.jwt.JwtTokenProvider;
import me.haneul.dto.CommentDTO;
import me.haneul.dto.CommentResponseDTO;
import me.haneul.entity.Board;
import me.haneul.entity.Comments;
import me.haneul.entity.Member;
import me.haneul.repository.BoardRepository;
import me.haneul.repository.CommentRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {
    private final JwtTokenProvider jwtTokenProvider;
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final BoardService boardService;

    public CommentService(CommentRepository commentRepository, BoardRepository boardRepository, JwtTokenProvider jwtTokenProvider, BoardService boardService) {
        this.commentRepository = commentRepository;
        this.boardRepository = boardRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.boardService = boardService;
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> selectAll() {  //작성자로 작성한 댓글 모두 조회
        return commentRepository.findByParentIdIsNull().stream().map(CommentResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> select() {  //작성자로 작성한 댓글 모두 조회
        //토큰에서 사용자 아이디 가져오기
        String id = SecurityContextHolder.getContext().getAuthentication().getName();

        return commentRepository.findByWriterOrderByIdDesc(id).stream().map(CommentResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public CommentResponseDTO select1(Long id) {  //댓글 모두 조회
        Comments comments = commentRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("댓글이 존재하지 않습니다.")
        );

        return new CommentResponseDTO(comments);
    }

    //댓글 작성
    @Transactional
    public Object insert(Long id, CommentDTO commentDTO, String name) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("게시물이 존재하지 않습니다.")
        );
        
        Comments commentsof = Comments.of(commentDTO, board, name);
        if(commentDTO.getParentid() == null) {  //대댓글이 아니라면 바로 저장
            Comments comments = commentRepository.save(commentsof);
            return new CommentResponseDTO(comments);
        }
        
        //대댓글이라면
        Comments parentComment = commentRepository.findByBoardIdAndId(id, commentDTO.getParentid()).orElseThrow(
                () -> new IllegalArgumentException("댓글이 존재하지 않습니다.")
        );  //해당 게시글에 해당 댓글이 있는지 확인
        parentComment.setChildComment(commentsof);  //기존 댓글에 대댓글 추가(parentComment에 childComment 추가)
        Comments comments = commentRepository.save(commentsof);  //childComment 저장
        
        return new CommentResponseDTO(comments);
        
    }

    //댓글 수정
    @Transactional
    public Comments revise(Long id, String contents) throws Exception {
        //토큰에서 사용자 아이디 가져오기
        String member_id = SecurityContextHolder.getContext().getAuthentication().getName();

        Comments comments = commentRepository.findById(id).orElseThrow(  //댓글이 존재하는지 확인
                () -> new IllegalArgumentException("댓글이 존재하지 않습니다.")
        );

        if(!comments.getWriter().equals(member_id))  //로그인한 회원과 작성자가 일치하는지 확인
            throw new Exception("회원이 작성자와 일치하지 않습니다.");

        comments.revise(contents);  //수정
        return comments;
    }

    //댓글 삭제
    @Transactional
    public void delete(Long id) throws Exception {
        Comments comments = commentRepository.findById(id).orElseThrow(  //댓글이 존재하는지 확인
                () -> new IllegalArgumentException("댓글이 존재하지 않습니다.")
        );

        //로그인한 회원과 작성자가 일치하는지 확인
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!comments.getWriter().equals(name))
            throw new Exception("회원이 작성자와 일치하지 않습니다.");

        commentRepository.delete(comments);
    }

    //댓글 여러개 삭제
    @Transactional
    public String delete_multiple(List<Long> id, HttpServletRequest request) throws Exception {
        //토큰에서 사용자 아이디 가져오기
        String member_id = SecurityContextHolder.getContext().getAuthentication().getName();

        int i = 0;
        while(i < id.size()) {
            Comments comments = commentRepository.findById(id.get(i)).orElseThrow(  //게시글이 존재하는지 확인
                    () -> new IllegalArgumentException("게시물이 존재하지 않습니다.")
            );

            if(!comments.getWriter().equals(member_id))  //로그인한 회원과 작성자가 일치하는지 확인
                return "회원이 작성자와 일치하지 않습니다.";

            commentRepository.delete(comments);
            i++;
        }

        return "게시물이 삭제되었습니다.";
    }
}
