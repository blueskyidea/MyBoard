package me.haneul.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.haneul.config.jwt.ApiResponseDTO;
import me.haneul.config.jwt.JwtTokenProvider;
import me.haneul.config.jwt.SuccessResponse;
import me.haneul.config.jwt.UserRoleEnum;
import me.haneul.dto.JwtDTO;
import me.haneul.dto.LoginDTO;
import me.haneul.dto.MemberDTO;
import me.haneul.entity.Member;
import me.haneul.entity.RefreshToken;
import me.haneul.repository.MemberRepository;
import me.haneul.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
//    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
//    private final BoardService boardService;
//    private final RefreshTokenRepository refreshTokenRepository;
//
//
//    @Transactional(readOnly = true)  //전체 조회
//    public List<MemberDTO> selectAll() {  //전체 조회
//        return memberRepository.findAll().stream().map(MemberDTO::new).toList();
//    }
//
//    @Transactional(readOnly = true)
//    public List<MemberDTO> select(String id) {  //작성자로 조회
//        //stream().map(MemberDto::new) -> dto로 바꿔줌, toList() -> List로 바꿔줌
//        return memberRepository.findById(id).stream().map(MemberDTO::new).toList();
//    }
//
    @Transactional(readOnly = true)
    public Map<String, Object> myPage() {  //작성자로 조회
        String id = SecurityContextHolder.getContext().getAuthentication().getName();

        //stream().map(MemberDto::new) -> dto로 바꿔줌, toList() -> List로 바꿔줌
        List<MemberDTO> memberDTOS = memberRepository.findById(id).stream().map(MemberDTO::new).toList();

        Map<String, Object> responseDTO = new HashMap<>();
        responseDTO.put("Member", memberDTOS);

        return responseDTO;
    }

    @Transactional
    public ApiResponseDTO<SuccessResponse> join(MemberDTO memberDTO) {  //회원가입
        String username = memberDTO.getId();
        String password = passwordEncoder.encode(memberDTO.getPassword());  //비밀번호 암호화

        // 입력한 username, password, admin 으로 user 객체 만들어 repository 에 저장(닉네임은 id와 똑같이 저장)
        UserRoleEnum role = memberDTO.getAdmin() ? UserRoleEnum.ADMIN : UserRoleEnum.USER;
        memberRepository.save(Member.of(username, password, memberDTO.getName(), memberDTO.getEmail(), username, role));

        return ApiResponseDTO.<SuccessResponse>builder().success(true).response(SuccessResponse.of(HttpStatus.OK, "회원가입 성공")).build();
    }

//    /*@Transactional
//    public MemberDTO join(MemberDTO memberDTO) {  //회원가입
//        String id = memberDTO.getId();
//        String password = passwordEncoder.encode(memberDTO.getPassword());
//
//        UserRoleEnum roleEnum = memberDTO.getAdmin() ? UserRoleEnum.ADMIN : UserRoleEnum.USER;
//
//        Member member = new Member(memberDTO);
//        //memberRepository.save(member);
//
//        memberRepository.save(Member.of(id, password, roleEnum));
//
//        return new MemberDTO(member);
//    }*/
//
//    //회원 수정
//    @Transactional
//    public String revise(MemberDTO memberDTO, HttpServletRequest request) throws Exception {
//        //토큰에서 사용자 아이디 가져오기
//        String id = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        //DB에서 회원 정보 가져오기
//        Member member = memberRepository.findById(id).orElseThrow(
//                () -> new IllegalArgumentException("회원이 존재하지 않습니다.")
//        );
//
//        String password;
//        if(memberDTO.getNewpassword() != null) {  //새로운 비밀번호가 있으면
//            //비밀번호 일치하는지 확인
//            if (!passwordEncoder.matches(memberDTO.getPassword(), member.getPassword())) {  //비밀번호 복호화
//                throw new Exception("비밀번호가 일치하지 않습니다.");
//            }
//            password = passwordEncoder.encode(memberDTO.getNewpassword()); //새로운 비밀번호 암호화
//            memberDTO.setPassword(password);  //암호화한 비밀번호 저장
//        }
//
//        //수정(비밀번호, 이름, 이메일 수정)
//        member.revise(memberDTO);
//        //return new MemberDTO(member);
//
//        return "수정이 완료되었습니다.";
//    }
//
//    //회원 삭제
//    @Transactional
//    public String delete(LoginDTO loginDTO, HttpServletRequest request) throws Exception {
//        //토큰에서 사용자 아이디 가져오기
//        String id = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        //DB에서 회원 정보 가져오기
//        Member member = memberRepository.findById(id).orElseThrow(
//                () -> new IllegalArgumentException("회원이 존재하지 않습니다.")
//        );
//        //비밀번호 일치하는지 확인
//        if(!passwordEncoder.matches(loginDTO.getPassword(), member.getPassword())) {  //비밀번호 복호화
//            throw new Exception("비밀번호가 일치하지 않습니다.");
//        }
//
//        //만약 해당 회원이 작성한 게시글이 있다면 전부 삭제
//        boardService.deleteAll(id);
//
//        //Redis에서 해당 회원 토큰 삭제
//        //refreshTokenRepository.delete(id);
//
//        //해당 회원 삭제
//        memberRepository.delete(member);
//
//        return "탈퇴가 완료되었습니다.";
//    }
//
    // 로그인
    @Transactional(readOnly = true)
    public ApiResponseDTO<SuccessResponse> login(LoginDTO loginDTO) {
        String username = loginDTO.getId();
        String password = loginDTO.getPassword();

        // 사용자 확인 & 비밀번호 확인
        Optional<Member> member = memberRepository.findById(username);
        if (member.isEmpty() || !passwordEncoder.matches(password, member.get().getPassword())) {
            return ApiResponseDTO.<SuccessResponse>builder().success(false).response(SuccessResponse.of(HttpStatus.OK, "로그인 실패")).build();
        }

//        //토큰 생성
//        JwtDTO jwt = jwtTokenProvider.createToken(member.get().getId(), member.get().getRole());
//
//        //RefreshToken은 DB에 저장
//        refreshTokenRepository.save(new RefreshToken(member.get().getId(), jwt.getRefreshToken()));
//
//        //쿠키 세팅
//        Cookie Accesscookie = new Cookie("AccessToken", jwt.getAccessToken());
//        Accesscookie.setMaxAge(60 * 60);
//        Accesscookie.setHttpOnly(true);
//        Accesscookie.setPath("/");
//        Cookie Refreshcookie = new Cookie("RefreshToken", jwt.getRefreshToken());
//        Refreshcookie.setMaxAge(60 * 60 * 24 * 30);
//        Refreshcookie.setHttpOnly(true);
//        Refreshcookie.setPath("/");
//
//        response.addCookie(Accesscookie);
//        response.addCookie(Refreshcookie);

        // header 에 들어갈 JWT 세팅
        //response.setHeader("AccessHeader", jwt.getAccessToken());
        //response.setHeader("RefreshHeader", jwt.getRefreshToken());

        return ApiResponseDTO.<SuccessResponse>builder().success(true).response(SuccessResponse.of(HttpStatus.OK, "로그인 성공")).build();

    }
//
//    /*@Transactional(readOnly = true)
//    public ApiResponseDTO<SuccessResponse> login(LoginDTO loginDTO, HttpServletResponse response) {
//        String id = loginDTO.getId();
//        String password = loginDTO.getPassword();
//
//        // 사용자 확인 & 비밀번호 확인
//        Optional<Member> member = memberRepository.findById(id);
//        if (member.isEmpty() || !passwordEncoder.matches(password, member.get().getPassword())) {
//            return ApiResponseDTO.<SuccessResponse>builder().success(false).response((SuccessResponse) response).build();
//        }
//
//        // header 에 들어갈 JWT 세팅
//        response.setHeader("Authorization", jwtTokenProvider.createToken(member.get().getId(), member.get().getRole()));
//
//        return ApiResponseDTO.<SuccessResponse>builder().success(true).response((SuccessResponse) response).build();
//    }*/
//
    public Member findById(Long userId) {
        return memberRepository.findById(String.valueOf(userId))
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}
