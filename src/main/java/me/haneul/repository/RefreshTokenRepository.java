package me.haneul.repository;

import me.haneul.entity.RefreshToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Repository
public class RefreshTokenRepository {
    private RedisTemplate redisTemplate;
    private final Logger logger = LoggerFactory.getLogger(RefreshTokenRepository.class);

    @Autowired
    public RefreshTokenRepository(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(RefreshToken refreshToken) {
        //opsFor은 특정 컬렉션의 작업들을 호출할 수 있는 인터페이스를 반환. 이것은 string을 위한 것임.
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        if(!Objects.isNull(valueOperations.get(refreshToken.getId()))) {
            //만약 이미 id를 key로 한 refreshtoken이 있을 경우, 업데이트를 위해 삭제.
            redisTemplate.delete(refreshToken.getId());
            logger.info("refreshToken Repository save -> update");
        }
        //Redis에 데이터 저장.
        valueOperations.set(refreshToken.getId(), refreshToken.getRefreshToken());
        //데이터 만료 시간 지정. 1개월
        redisTemplate.expire(refreshToken.getId(), 60 * 60 * 24 * 30 * 1000L, TimeUnit.SECONDS);
    }

    public Optional<RefreshToken> findById(String id){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        //id를 key로 가진 데이터 찾음.
        String refreshToken = valueOperations.get(id);

        if(refreshToken == null) {
            return Optional.empty();
        } else {
            return Optional.of(new RefreshToken(id, refreshToken));
        }
    }

    public Optional<RefreshToken> findByRefreshToken(String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        //refreshToken를 key로 가진 데이터 찾음.
        String id = valueOperations.get(refreshToken);

        if(id == null) {
            return Optional.empty();
        } else {
            return Optional.of(new RefreshToken(id, refreshToken));
        }
    }

    public void delete(String id) {
        redisTemplate.delete(id);
    }
}
