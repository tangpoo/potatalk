package com.potatalk.memberservice.config.jwt;

import com.potatalk.memberservice.exception.UnauthorizedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class JwtWebFilter implements WebFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
        String requestPath = exchange.getRequest().getURI().getPath();

        if(isPermitAllPath(requestPath)) {
            return chain.filter(exchange);
        }

        String token = jwtTokenProvider.getTokenFromHeader(exchange.getRequest());

        if (token != null && jwtTokenProvider.validateToken(token)) {
            return jwtTokenProvider.getAuthentication(token)
                .flatMap(authentication -> {
                    return chain.filter(exchange)
                        .contextWrite(
                            ReactiveSecurityContextHolder.withAuthentication(authentication));
                });
        } else if (token != null) {
            return Mono.error(new UnauthorizedException("Invalid JWT token"));
        }

        return chain.filter(exchange);
    }

    private final List<String> permitAllPaths = List.of(
        "/api/v1/..."
    );

    private boolean isPermitAllPath(String path) {
//        return permitAllPaths.stream().anyMatch(path::startsWith);
        return true;
    }
}
