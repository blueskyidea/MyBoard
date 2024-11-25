package me.haneul.config.oauth;


import lombok.RequiredArgsConstructor;
import me.haneul.config.jwt.UserRoleEnum;
import me.haneul.entity.Member;
import me.haneul.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    private final Logger logger = LoggerFactory.getLogger(OAuth2UserCustomService.class);
    private final MemberRepository memberRepository;

    /* loadUser(): DefaultOAuth2UserService에서 제공하는 OAuth 서비스에서 제공하는 정보를 기반으로 유저 객체를 만들어주는 메서드
     * loadUser()를 통해 사용자 객체를 불러오는데 여기에는 식별자, 이름, 이메일, 프로필 사진 링크 등의 정보를 담고있음. */
    //리소스 서버에서 보내주는 사용자 정보를 불러오는 메서드
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();  //공급자 ID

        //각 공급자에 따라 다르게 처리
        OAuth2User user = super.loadUser(userRequest);
        saveOrUpdate(user);

        return user;
    }

    //유저가 있으면 업데이트, 없으면 유저 생성
    private Member saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        //카카오의 경우 이메일은 kakao_account 객체 내부에 존재
        String id, email, name;

        if(attributes.containsKey("kakao_account")) {
            id = (String) attributes.get("id");
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");  //이메일 정보 가져오기

            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            name = (String) properties.get("nickname");  //이름 정보 가져오기
        } else {  //Google 등 다른 제공자의 경우
            id = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        }

        //아이디, 이메일, 이름이 null인지 확인
        if(id == null || id.isEmpty()) {
            throw new IllegalArgumentException("아이디 정보가 없습니다.");
        }
        if(email == null || email.isEmpty()) {
            throw new IllegalArgumentException("이메일 정보가 없습니다.");
        }
        if(name == null) {
            throw new IllegalArgumentException("이름 정보가 없습니다.");
        }

        Member member = memberRepository.findByEmail(email)
                .map(entity -> entity.update(id, name))
                .orElse(Member.builder()
                        .id(id)
                        .email(email)
                        .name(name)
                        .nickname(id)
                        .role(UserRoleEnum.USER)
                        .build());

        return memberRepository.save(member);
    }
}
