package me.haneul.service;

import jakarta.servlet.http.HttpServletRequest;
import me.haneul.config.jwt.JwtTokenProvider;
import me.haneul.dto.AddBoardRequest;
import me.haneul.dto.BoardResponseDTO;
import me.haneul.dto.CommentResponseDTO;
import me.haneul.dto.InsertDTO;
import me.haneul.entity.Board;
import me.haneul.entity.Comments;
import me.haneul.entity.Member;
import me.haneul.repository.BoardRepository;
import me.haneul.repository.CommentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BoardService {
    private final JwtTokenProvider jwtTokenProvider;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    public BoardService(BoardRepository boardRepository, CommentRepository commentRepository, JwtTokenProvider jwtTokenProvider) {
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional(readOnly = true)
    public List<BoardResponseDTO> selectAll() {  //전체 조회
        return boardRepository.findAll().stream().map(BoardResponseDTO::new).toList();
    }

    //게시글+댓글 조회
    @Transactional(readOnly = true)
    public Map<String, Object> selectId(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("게시물이 존재하지 않습니다.")
        );
        BoardResponseDTO boardResponseDTO = new BoardResponseDTO(board);

        List<CommentResponseDTO> commentResponseDTO = commentRepository.findByBoardIdAndParentIdIsNull(id).stream().map(CommentResponseDTO::new).toList();

        Map<String, Object> responseDTO = new HashMap<>();
        responseDTO.put("Board", boardResponseDTO);
        responseDTO.put("Comments", commentResponseDTO);

        return responseDTO;
    }

    @Transactional(readOnly = true)
    public List<BoardResponseDTO> select() {  //작성자로 조회
        //토큰에서 사용자 아이디 가져오기
        String id = SecurityContextHolder.getContext().getAuthentication().getName();

        //stream().map(BoardResponseDto::new) -> dto로 바꿔줌, toList() -> List로 바꿔줌
        return boardRepository.findByWriterOrderByIdDesc(id).stream().map(BoardResponseDTO::new).toList();
    }

    @Transactional
    public Board insert(AddBoardRequest request, String writer) {  //게시글 작성
        return boardRepository.save(request.toEntity(writer));
    }

    //게시글 수정
    @Transactional
    public Board revise(Long id, InsertDTO insertDTO) throws Exception {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));

        //로그인한 회원과 작성자가 일치하는지 확인
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!board.getWriter().equals(name))
           throw new Exception("회원이 작성자와 일치하지 않습니다.");

        board.revise(insertDTO);  //수정(제목, 내용만 수정 가능)
        return board;
    }

    //게시글 삭제
    @Transactional
    public void delete(Long id) throws Exception {
        Board board = boardRepository.findById(id).orElseThrow(  //게시글이 존재하는지 확인
                () -> new IllegalArgumentException("게시물이 존재하지 않습니다.")
        );

        //로그인한 회원과 작성자가 일치하는지 확인
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!board.getWriter().equals(name))
            throw new Exception("회원이 작성자와 일치하지 않습니다.");

        boardRepository.delete(board);
    }

    //전부 삭제(회원 삭제 시 사용)
    @Transactional
    public boolean deleteAll(String id) throws Exception {
        List<Board> boards = Optional.ofNullable(boardRepository.findByWriter(id))
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));

        boardRepository.deleteAllInBatch(boards);  //게시물 모두 삭제

        List<Comments> comments = Optional.ofNullable(commentRepository.findByWriter(id))
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));

        commentRepository.deleteAllInBatch(comments);  //댓글 모두 삭제

        return true;
    }

//    //토큰 반환해줌(AccessToken이 만료돼서 RefreshToken으로 로그인 했을 경우 사용하기 위해 구현)
//    public String JwtToken(HttpServletRequest request) {
//        String token = jwtTokenProvider.resolveAccessToken(request);  //AccessToken 가져옴
//        if(token == null)  //만약 AccessToken이 만료됐거나 없다면
//            token = jwtTokenProvider.resolveRefreshToken(request);  //RefreshToken 가져옴
//
//        return token;
//    }

    public Board findById(Long Id) {
        return boardRepository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}
