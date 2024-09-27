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

            if (path.equals("/api/v1/members/signup") || path.equals("/api/v1/members/signin")) {
                log.info("pass filter");
                return chain.filter(exchange);
            }

            String tokenValue = jwtUtil.getTokenFromHeader(request);

            log.info("in filter: " + tokenValue);

            if (StringUtils.hasText(tokenValue)) {
                try {
                    Claims payload = jwtUtil.getUserInfoFromToken(tokenValue);

                    String username = payload.getSubject();
//                    String username = payload.get("username", String.class);

                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
//                        .header("X-User-Email", email)
                        .header("X-Username", username)
                        .build();

                    log.info(String.format("User %s joined the server", username));

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                } catch (Exception e) {
                    log.error("JWT 검증 실패: {}", e.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
        //config
    }
}
