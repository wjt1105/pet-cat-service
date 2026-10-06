package com.petsitter.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petsitter.common.result.Result;
import com.petsitter.common.result.ResultCode;
import com.petsitter.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 全局 JWT 鉴权过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /** 鉴权白名单：登录、注册 */
    private static final String[] WHITE_LIST = {
            "/api/user/login",
            "/api/user/register"
    };

    /** 透传给下游服务的请求头 */
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USERNAME = "X-Username";

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // CORS 预检请求与白名单直接放行
        if (HttpMethod.OPTIONS.equals(request.getMethod()) || isWhiteList(path)) {
            return chain.filter(exchange);
        }

        String token = resolveToken(request);
        if (token == null || !JwtUtils.validate(token)) {
            return writeUnauthorized(exchange.getResponse());
        }

        // 解析用户信息并透传下游
        Claims claims = JwtUtils.parseClaims(token);
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(HEADER_USER_ID, String.valueOf(claims.get("userId")))
                .header(HEADER_USERNAME, String.valueOf(claims.get("username")))
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private boolean isWhiteList(String path) {
        for (String pattern : WHITE_LIST) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private String resolveToken(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith(JwtUtils.TOKEN_PREFIX)) {
            return authorization.substring(JwtUtils.TOKEN_PREFIX.length());
        }
        return null;
    }

    private Mono<Void> writeUnauthorized(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body;
        try {
            body = objectMapper.writeValueAsBytes(Result.error(ResultCode.UNAUTHORIZED));
        } catch (JsonProcessingException e) {
            log.error("未授权响应序列化失败", e);
            body = ("{\"code\":" + ResultCode.UNAUTHORIZED.getCode()
                    + ",\"message\":\"" + ResultCode.UNAUTHORIZED.getMessage() + "\"}")
                    .getBytes(StandardCharsets.UTF_8);
        }
        DataBuffer buffer = response.bufferFactory().wrap(body);
        return response.writeWith(Mono.just(buffer));
    }
}
