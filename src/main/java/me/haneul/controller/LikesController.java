package me.haneul.controller;

import me.haneul.service.LikesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@Tag(name = "좋아요, 싫어요", description = "게시글이나 댓글에 좋아요, 싫어요 관련 API")
public class LikesController {
    private final LikesService likesService;

    @PostMapping("/like/{id}")  //게시글 좋아요, 싫어요 저장(id: 게시글 번호)
    public ResponseEntity<String> boardlike(@PathVariable(name = "id") Long id, @RequestBody Boolean like) throws Exception {
        try {
            likesService.likes(id, like);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            // 실패 시 오류 메시지 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("comment/like/{id}")  //댓글 좋아요, 싫어요 저장(id: 댓글 번호)
    public ResponseEntity<String> commentlike(@PathVariable(name = "id") Long id, @RequestBody Boolean like) {
        try {
            likesService.commentlikes(id, like);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
