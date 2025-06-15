package com.itxiaoqi.userservice.entity.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String email;
    private String phone;
}
