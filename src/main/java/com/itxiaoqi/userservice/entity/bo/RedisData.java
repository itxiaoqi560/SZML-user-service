package com.itxiaoqi.userservice.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisData<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private T data;//缓存数据
    private LocalDateTime expireTime;//过期时间
}