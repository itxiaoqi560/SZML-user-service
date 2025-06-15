package com.itxiaoqi.userservice.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String email;
    private String phone;
}
