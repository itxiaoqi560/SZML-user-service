package com.itxiaoqi.userservice.entity.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itxiaoqi.userservice.constant.Constant;
import com.itxiaoqi.userservice.context.UserIdContext;
import com.itxiaoqi.userservice.utils.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer code;//1成功，0失败
    private String msg;
    private DataObject<T> data;
    public static <T> Result<T> success() {
        return new Result<>(1, "请求成功", new DataObject<>(null,null));
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(1, "请求成功", new DataObject<>(data,null));
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(0, msg, new DataObject<>(null,null));
    }

    public static <T> Result<T> successWithToken(T data){
        String token = getToken();
        return new Result<>(1,"请求成功",new DataObject<>(data,token));
    }

    public static <T> Result<T> successWithToken(){
        String token = getToken();
        return new Result<>(1,"请求成功",new DataObject<>(null,token));
    }

    @AllArgsConstructor
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DataObject<T> implements Serializable {
        T data;
        String token;
    }

    private static String getToken(){
        Map<String, Object> mp = new HashMap<>();
        mp.put(Constant.ID, UserIdContext.getId());
        String token = JwtUtil.createJWT(Constant.SECRET_KEY, Constant.TOKEN_EXPIRE, mp);
        return token;
    }
}