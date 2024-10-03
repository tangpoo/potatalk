package com.potatalk.filter;

import com.potatalk.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class JwtAuthorizationGatewayFilterFactory extends
    AbstractGatewayFilterFactory<JwtAuthorizationGatewayFilterFactory.Config> {

    private final JwtUtil jwtUtil;

    @Autowired
    public JwtAuthorizationGatewayFilterFactory(JwtUtil jwtUtil) {
        super(JwtAuthorizationGatewayFilterFactory.Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(JwtAuthorizationGatewayFilterFactory.Config config) {
        return (exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            log.info("in filter");

            // 로그인과 회원가입 경로는 필터를 통과
            if (path.equals("/api/v1/members/signup") || path.equals("/api/v1/members/signin")) {
                log.info("pass filter");
                return chain.filter(exchange);
            }

            // 헤더에서 토큰 추출
            String tokenValue = jwtUtil.getTokenFromHeader(request);

            log.info("in filter with token: " + tokenValue);

            // 토큰이 없을 경우 Unauthorized 응답 설정
            if (!StringUtils.hasText(tokenValue)) {
                log.error("JWT 토큰이 존재하지 않음");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // 토큰이 있을 경우 검증
            try {
                Claims payload = jwtUtil.getUserInfoFromToken(tokenValue);

                String username = payload.getSubject();

                // 토큰의 정보를 요청 헤더에 추가
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-Username", username)
                    .build();

                log.info(String.format("User %s joined the server", username));

                // 수정된 요청으로 필터 체인 통과
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } catch (Exception e) {
                // 토큰 검증 실패 시 Unauthorized 응답 설정
                log.error("JWT 검증 실패: {}", e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    public static class Config {
        //config
    }
}
