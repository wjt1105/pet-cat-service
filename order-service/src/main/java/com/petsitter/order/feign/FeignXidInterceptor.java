package com.petsitter.order.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.seata.core.context.RootContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Feign 请求拦截器：把当前全局事务 XID 放入请求头透传给下游服务，
 * 下游 Seata 过滤器取出 XID 绑定到 RootContext，从而加入同一个全局事务。
 */
@Component
public class FeignXidInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String xid = RootContext.getXID();
        if (StringUtils.hasText(xid)) {
            template.header(RootContext.KEY_XID, xid);
        }
    }
}
