package me.haneul.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class JwtDTO {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
