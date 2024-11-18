package me.haneul.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Setter
@NoArgsConstructor
@Builder
@RedisHash(value = "refreshToken")
public class RefreshToken {
    @Id
    private String id;

    @Indexed
    private String refreshToken;

    public RefreshToken(String id, String refreshToken) {
        this.id = id;
        this.refreshToken = refreshToken;
    }
    public RefreshToken updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

}
