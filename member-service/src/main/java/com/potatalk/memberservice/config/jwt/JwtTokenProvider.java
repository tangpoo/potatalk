package com.potatalk.memberservice.config.jwt;

import com.potatalk.memberservice.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

//    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";
//    private final MemberRepository memberRepository;
    private final long TOKEN_TIME = 30 * 60 * 1000L;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public Mono<String> createToken(String username) {
        return Mono.fromCallable(() -> {
            Date expireDate = createExpireDate(TOKEN_TIME);

            return BEARER_PREFIX +
                Jwts.builder()
                    .setSubject(username)
                    .claim(AUTHORIZATION_KEY, null)
                    .setExpiration(expireDate)
                    .setIssuedAt(new Date())
                    .signWith(key, signatureAlgorithm)
                    .compact();
        });
    }

    private Date createExpireDate(long expireDate) {
        long curTime = (new Date()).getTime();
        return new Date(curTime + expireDate);
    }

}
