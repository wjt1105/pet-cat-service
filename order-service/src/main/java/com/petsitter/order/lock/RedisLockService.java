package com.petsitter.order.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

/**
 * 基于 Redis + Lua 的分布式锁
 * <p>
 * 加锁：SETNX + PEXPIRE 放在同一个 Lua 脚本中原子执行，抢到锁才设置过期时间（防止死锁）。
 * 释放：先 GET 判断持有者是否为自己，是自己才 DEL，避免误删别人的锁。
 */
@Service
@RequiredArgsConstructor
public class RedisLockService {

    /** 库存锁 key 前缀 */
    private static final String STOCK_LOCK_PREFIX = "lock:order:stock:";

    /** 默认锁过期时间 30 秒（兜底，即使持有方宕机也能自动释放） */
    public static final long DEFAULT_EXPIRE_MILLIS = 30_000L;

    /**
     * 加锁脚本：抢到锁（setnx 返回 1）才设置过期时间
     */
    private static final String LOCK_SCRIPT =
            "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then " +
            "    return redis.call('pexpire', KEYS[1], ARGV[2]) " +
            "else " +
            "    return 0 " +
            "end";

    /**
     * 释放锁脚本：持有者必须是自己才删除
     */
    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('del', KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end";

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 生成一次锁的唯一持有者标识
     */
    public String newLockValue() {
        return UUID.randomUUID().toString();
    }

    /**
     * 构造某服务的库存锁 key
     */
    public String buildStockLockKey(Long serviceId) {
        return STOCK_LOCK_PREFIX + serviceId;
    }

    /**
     * 尝试加锁（非阻塞，抢不到立即返回 false）
     *
     * @param key          锁 key
     * @param lockValue    持有者标识（释放时用于校验）
     * @param expireMillis 锁过期时间（毫秒）
     * @return true-加锁成功
     */
    public boolean tryLock(String key, String lockValue, long expireMillis) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(LOCK_SCRIPT, Long.class);
        Long result = stringRedisTemplate.execute(script,
                Collections.singletonList(key), lockValue, String.valueOf(expireMillis));
        return result != null && result == 1L;
    }

    /**
     * 释放锁
     *
     * @return true-成功释放
     */
    public boolean unlock(String key, String lockValue) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
        Long result = stringRedisTemplate.execute(script,
                Collections.singletonList(key), lockValue);
        return result != null && result == 1L;
    }
}
