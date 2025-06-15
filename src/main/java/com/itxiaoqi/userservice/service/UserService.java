package com.itxiaoqi.userservice.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.itxiaoqi.userservice.entity.po.User;
import com.itxiaoqi.userservice.entity.vo.UserVO;

import java.util.List;

public interface UserService extends IService<User> {
    List<UserVO> getUsers(Integer page,Integer pageSize);
}
