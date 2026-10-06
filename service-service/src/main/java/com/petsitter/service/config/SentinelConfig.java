package com.petsitter.service.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petsitter.common.result.Result;
import com.petsitter.common.result.ResultCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sentinel 配置：URL 级规则触发时返回统一 JSON（默认仅返回纯文本 Blocked by Sentinel）
 */
@Configuration
public class SentinelConfig {

    /** 429 Too Many Requests（该常量 Servlet 5.0 才提供，javax.servlet 4.0 中直接用字面值） */
    private static final int TOO_MANY_REQUESTS = 429;

    @Bean
    public BlockExceptionHandler blockExceptionHandler(ObjectMapper objectMapper) {
        return (request, response, ex) -> {
            response.setStatus(TOO_MANY_REQUESTS);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(
                    Result.error(ResultCode.SERVICE_DEGRADED)));
        };
    }
}
