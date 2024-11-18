package me.haneul.controller;

import me.haneul.dto.CommentDTO;
import me.haneul.dto.CommentResponseDTO;
import me.haneul.entity.Comments;
import me.haneul.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/comment")
@Tag(name = "댓글", description = "댓글 관련 API")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/select")  //모든 댓글 조회
    public List<CommentResponseDTO> selectAll() {
        return commentService.selectAll();
    }

    @GetMapping("/select/{id}")  //특정 아이디가 작성한 게시글 조회(로그인 해야함)
    public CommentResponseDTO select1(@PathVariable Long id) {
        return commentService.select1(id);
    }

    @Operation(summary = "댓글 작성", description = "댓글 작성 시 사용하는 API")
    @Parameter(name = "board_id", description = "게시글 번호", example = "1")
    @PostMapping("/post/{id}")  //댓글 작성(id: 게시글 번호)
    public String post(@PathVariable(name = "id") Long id, @ModelAttribute CommentDTO commentDTO, Principal principal) {
        System.out.println("댓글 작성 게시글 번호: " + id + "내용: " + commentDTO.getContents());
        commentService.insert(id, commentDTO, principal.getName());
        return "redirect:/select/Id/" + id;
    }

    @Operation(summary = "댓글 수정", description = "댓글 수정 시 사용하는 API")
    @Parameter(name = "id", description = "댓글 번호", example = "1")
    @PutMapping("/revise/{id}")  //댓글 수정(id: 댓글 번호, 로그인 해야함)
    public ResponseEntity<Object> revise(@PathVariable(name = "id") Long id, @RequestBody Map<String, String> requestBody) throws Exception {
        String contents = requestBody.get("contents");
        try {
            Comments comments = commentService.revise(id, contents);
            return ResponseEntity.ok().body(comments);
        }
        catch (Exception e) {  //예외(게시물이 존재하지 않음, 비밀번호 일치하지 않음)가 발생하면 예외 메시지 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(summary = "댓글 삭제", description = "댓글 삭제 시 사용하는 API")
    @Parameter(name = "id", description = "댓글 번호", example = "1")
    @DeleteMapping("/delete/{id}")  //댓글 삭제
    public ResponseEntity<Object> delete(@PathVariable(name = "id") Long id) throws Exception {
        try {
            commentService.delete(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(summary = "댓글 여러개 삭제", description = "댓글 여러개 삭제 시 사용하는 API")
    @Parameter(name = "id", description = "댓글 번호", example = "1")
    @DeleteMapping("/delete/multiple")  //댓글 여러개 삭제
    public Object delete(@Validated @RequestBody List<Long> id, HttpServletRequest request) throws Exception {
        try {
            System.out.println(id);
            return commentService.delete_multiple(id, request);
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }
}
