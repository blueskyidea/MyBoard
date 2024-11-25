package me.haneul.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.haneul.dto.BoardResponseDTO;
import me.haneul.dto.CommentResponseDTO;
import me.haneul.dto.MemberDTO;
import me.haneul.service.BoardService;
import me.haneul.service.CommentService;
import me.haneul.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/myPage")
@Tag(name = "마이페이지", description = "마이페이지 관련 API")
public class MyPageController {
    private final BoardService boardService;
    private final CommentService commentService;
    private final MemberService memberService;

    @GetMapping("/my")  //로그인 시 로그인 한 사람 정보 조회(마이페이지)
    public String myPage(Model model) {
        Map<String, Object> memberDetail = memberService.myPage();
        model.addAttribute("members", memberDetail.get("Member"));
        return "mypage";
    }

    @GetMapping("/myPost")  //특정 아이디가 작성한 게시글 조회
    //내용 DTO에 담아 List 형태로 Client에 보냄.
    public String myPost(Model model) {
        List<BoardResponseDTO> posts = boardService.select();
        model.addAttribute("posts", posts);
        return "myposts";
    }

    @GetMapping("/myComment")  //특정 아이디가 작성한 댓글 조회
    public String myComment(Model model) {
        List<CommentResponseDTO> comments = commentService.select();
        model.addAttribute("comments", comments);
        return "mycomments";
    }

    //프로필 편집(회원 수정)
    @GetMapping("/revisePage")
    public String revisePage() {
        return "editprofile";
    }
    @PutMapping("/reviseMember")
    public Object revise(@RequestBody MemberDTO memberDTO) throws Exception {
        try {
            String msg = memberService.revise(memberDTO);
            return ResponseEntity.ok(msg);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
