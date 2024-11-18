package me.haneul.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String key = "AccessToken";
        String refreshkey = "RefreshToken";

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(key).addList(refreshkey);

        SecurityScheme accessTokenSecurityScheme = new SecurityScheme()
                .name(key)
                .type(SecurityScheme.Type.APIKEY)
                .scheme("bearer")
                .in(SecurityScheme.In.HEADER)
                .bearerFormat("JWT");

        SecurityScheme refreshTokenSecurityScheme = new SecurityScheme()
                .name(refreshkey)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER);

        Components components = new Components()
                .addSecuritySchemes(key, accessTokenSecurityScheme)
                .addSecuritySchemes(refreshkey, refreshTokenSecurityScheme);

        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .components(components);
    }
    private Info apiInfo() {
        return new Info()
                .title("게시판 프로젝트") // API의 제목
                .description("로그인(회원가입)-JWT, 게시글.댓글(조회, 작성, 수정, 삭제, 좋아요.싫어요) 기능 구현") // API에 대한 설명
                .version("1.0.0"); // API의 버전
    }
}
