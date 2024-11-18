package me.haneul.entity;

import me.haneul.config.jwt.UserRoleEnum;
import me.haneul.dto.MemberDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Member")
public class Member implements UserDetails { //UserDetails(스프링 시큐리티에서 사용자의 인증 정보를 담아두는 인터페이스)를 상속받아 인증 객체로 사용
    @Id
    String id;

    @Column
    String password;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    String email;

    @Column(unique = true, nullable = false)
    String nickname;

    @Column(nullable = false, length = 30)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    public Member(MemberDTO memberDTO) {  //DTO를 받아서 DB에 저장
        this.id = memberDTO.getId();
        this.password = memberDTO.getPassword();
        this.name = memberDTO.getName();
        this.email = memberDTO.getEmail();
    }

    //사용자 정보 수정
    public void revise(MemberDTO memberDTO) {
        if(memberDTO.getPassword() != null)
            this.password = memberDTO.getPassword();
        if(memberDTO.getName() != null)
            this.name = memberDTO.getName();
        if(memberDTO.getEmail() != null)
            this.email = memberDTO.getEmail();
        if(memberDTO.getNickname() != null)
            this.nickname = memberDTO.getNickname();
    }

    //사용자 이름 변경
    public Member update(String id, String name) {
        this.id = id;
        this.name = name;
        return this;
    }

    @Builder
    private Member(String id, String password, String name, String email, String nickname, UserRoleEnum role) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }

    public static Member of(String id, String password, String name, String email, String nickname, UserRoleEnum role) {
        return Member.builder().id(id).password(password).name(name).email(email)
                .nickname(nickname).role(role).build();
    }


    @Override  //사용자가 가지고 있는 권한 목록 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }

    //사용자의 id를 반환(고유한 값)
    @Override
    public String getUsername() {
        return id;
    }

    //사용자의 패스워드 반환
    @Override
    public String getPassword() {
        return password;
    }

    //계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        //만료되었는지 확인하는 로직
        return true;  //ture -> 만료되지 않았음
    }

    //계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        //계정이 잠금되었는지 확인하는 로직
        return true;  //true -> 잠금되지 않았음
    }

    //패스워드의 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        //패스워드가 만료되었는지 확인하는 로직
        return true;  //true -> 만료되지 않았음
    }

    //계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        //계정이 사용 가능한지 확인하는 로직
        return true;  //true -> 사용 가능
    }
}
