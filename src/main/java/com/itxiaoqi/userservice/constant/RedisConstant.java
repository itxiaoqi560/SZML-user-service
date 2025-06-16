package com.itxiaoqi.userservice.constant;

import java.util.List;
import java.util.Objects;

public abstract class RedisConstant {
    /**
     * 登录状态key
     */
    public static final String LOGIN_KEY = "LOGIN:";
    /**
     * 锁key
     */
    public static final String LOCK_KEY = "LOCK:";
    /**
     * 令牌保存时间，单位毫秒
     */
    public static final Long TOKEN_EXPIRE = 24 * 60 * 60 * 1000L;//计时单位：毫秒
}
