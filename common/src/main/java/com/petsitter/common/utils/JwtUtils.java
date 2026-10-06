package com.petsitter.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 工具类
 */
public final class JwtUtils {

    /** 请求头名称 */
    public static final String HEADER = "Authorization";

    /** Token 前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** 签名密钥（HS256 要求不少于 32 字节） */
    private static final String SECRET = "petsitter-cat-feeding-platform-secret-key-2026";

    /** 有效期 24 小时 */
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000L;

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_USERNAME = "username";

    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private JwtUtils() {
    }

    /**
     * 生成 Token
     */
    public static String createToken(Long userId, String username) {
        Date now = new Date();
        return Jwts.builder()
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_USERNAME, username)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + EXPIRATION_MS))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token
     */
    public static Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static Long getUserId(String token) {
        return parseClaims(token).get(CLAIM_USER_ID, Long.class);
    }

    public static String getUsername(String token) {
        return parseClaims(token).get(CLAIM_USERNAME, String.class);
    }

    /**
     * 校验 Token 是否有效
     */
    public static boolean validate(String token) {
        try {
            return !parseClaims(token).getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
