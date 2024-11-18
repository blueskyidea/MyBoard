package me.haneul.service;

import lombok.RequiredArgsConstructor;
import me.haneul.config.jwt.JwtTokenProvider;
import me.haneul.entity.RefreshToken;
import me.haneul.repository.RefreshTokenRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }

    @Transactional
    public void delete() {
//        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
//        String userId = String.valueOf(jwtTokenProvider.getUserId(token));

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        refreshTokenRepository.delete(userId);
    }
}
