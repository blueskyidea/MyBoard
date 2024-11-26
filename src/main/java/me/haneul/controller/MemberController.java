package me.haneul.controller;

import me.haneul.dto.MemberDTO;
import me.haneul.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.haneul.util.CookieUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
@Tag(name = "회원", description = "회원 관련 API")
public class MemberController {
//
    private final MemberService memberService;
//
//    @GetMapping("/select")  //회원 전체 조회
//    public List<MemberDTO> selectAll() { return memberService.selectAll(); }
//
//    @GetMapping("/select/{id}")  //아이디로 회원 조회
//    public List<MemberDTO> select(@PathVariable String id) { return memberService.select(id); }
//
    //회원가입
    @GetMapping("/joinPage")
    public String joinPage() {
        return "join";
    }
    @Operation(summary = "회원가입", description = "회원가입 시 사용하는 API")
    @PostMapping("/join")  //회원 가입
    public String join(@ModelAttribute MemberDTO memberDTO) {
        if(memberService.join(memberDTO).getResponse().getMessage().equals("회원가입 성공")) {
            return "redirect:/member/loginPage";
        }
        else {
            return "redirect:/member/joinPage";
        }
    }

    //로그인
    @GetMapping("/loginPage")
    public String loginPage() {
        return "login";
    }
    @GetMapping("/oauth2loginPage")
    public String oauth2loginPage() {
        return "login";
    }

//    @Operation(summary = "로그인", description = "로그인 시 사용하는 API, AccessToken은 1시간 유효, RefreshToken은 1개월 유효")
//    @PostMapping("/login")
//    public String login(@ModelAttribute LoginDTO loginDTO) {
//        if(memberService.login(loginDTO).getResponse().getMessage().equals("로그인 성공")) {
//            return "redirect:/select";
//        }
//        else {
//            return "redirect:/member/loginPage";
//        }
//    }
    //로그아웃
    @GetMapping("/logoutPage")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("로그아웃 페이지");

        CookieUtil.deleteCookie(request, response, "access_token");
        CookieUtil.deleteCookie(request, response, "refresh_token");

        return "redirect:/select";
    }
}
