package me.haneul.dto;

import me.haneul.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {
    private String id;
    private String password;
    private String newpassword;
    private String name;
    private String email;
    private String nickname;
    private Boolean admin;
    private String role;

    public MemberDTO() {}

    public MemberDTO(Member member) {  //Member 엔티티를 받아서 MemberDTO 객체로 만들기 위한 생성자
        this.id = member.getId();
        this.password = member.getPassword();
        this.name = member.getName();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.role = member.getRole().getAuthority();
    }
}
