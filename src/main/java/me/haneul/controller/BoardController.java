package me.haneul.controller;

import me.haneul.dto.AddBoardRequest;
import me.haneul.dto.BoardResponseDTO;
import me.haneul.dto.BoardWithNicknameDTO;
import me.haneul.dto.InsertDTO;
import me.haneul.entity.Board;
import me.haneul.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Controller
@RequiredArgsConstructor
//@RestController
@Tag(name = "게시글", description = "게시글 관련 API")
public class BoardController {
    private final BoardService boardService;

    //게시글 전체 조회
    @GetMapping("/select")  //게시글 전체 조회
    @Operation(summary = "게시글 전체 조회", description = "게시글(제목+작성자) 조회 시 사용하는 API")
    public String selectAll(Model model) {
        List<BoardWithNicknameDTO> posts = boardService.selectAll();
        model.addAttribute("posts", posts);
        return "board";
    }

    //특정 게시글+댓글 조회
    @GetMapping("/select/Id/{Id}")
    @Operation(summary = "특정 게시글 조회", description = "하나의 게시글 선택 후 조회 시 사용하는 API")
    @Parameter(name = "board_id", description = "게시글 번호", example = "1")
    public String selectId(@PathVariable(name = "Id") Long Id, Model model) {
        Map<String, Object> postDetail = boardService.selectId(Id);
        model.addAttribute("postDetail", postDetail.get("Board"));
        model.addAttribute("comments", postDetail.get("Comments"));
        return "boardDetail";
    }

    //게시글 작성+수정
    @GetMapping("/postPage")
    //id 키를 가진 쿼리 파라미터의 값을 id 변수에 매핑(id는 없을 수도 있음)
    public String postPage(@RequestParam(required = false, name = "id") Long id, Model model) {
        if(id == null) {  //id가 없으면 생성
            model.addAttribute("post", new InsertDTO());
        } else { //id가 있으면 수정
            Board board = boardService.findById(id);
            model.addAttribute("post", board);
        }

        return "post";
    }
    @Operation(summary = "게시글 작성", description = "게시글 작성 시 사용하는 API(로그인 필수)")
    @PostMapping("/post")  //게시글 작성
    public ResponseEntity<Board> post(@RequestBody @Validated AddBoardRequest request, Principal principal) {
        Board board = boardService.insert(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(board);
    }
    @Operation(summary = "게시글 수정", description = "게시글 수정 시 사용하는 API(로그인 필수)")
    @PutMapping("/revise/{id}")
    public ResponseEntity<Board> revise(@PathVariable(name = "id") Long id, @RequestBody InsertDTO insertDTO) throws Exception {
        Board board = boardService.revise(id, insertDTO);
        return ResponseEntity.ok().body(board);
    }

    @Operation(summary = "게시글 삭제", description = "게시글 삭제 시 사용하는 API(로그인 필수)")
    @DeleteMapping("/delete/{id}")  //게시글 삭제
    public ResponseEntity<String> delete(@PathVariable(name = "id") Long id) throws Exception {
        try {
            boardService.delete(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public String search(@RequestParam("type") String type, @RequestParam("keyword") String keyword, Model model) {
        List<BoardWithNicknameDTO> posts = boardService.search(type, keyword);
        model.addAttribute("posts", posts);
        return "board";
    }
}
