package com.itxiaoqi.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itxiaoqi.userservice.client.PermissionClient;
import com.itxiaoqi.userservice.context.UserIdContext;
import com.itxiaoqi.userservice.entity.po.User;
import com.itxiaoqi.userservice.entity.result.PageResult;
import com.itxiaoqi.userservice.entity.vo.UserVO;
import com.itxiaoqi.userservice.mapper.UserMapper;
import com.itxiaoqi.userservice.service.UserService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private PermissionClient permissionClient;

    @GlobalTransactional
    @Override
    public List<UserVO> getUsers(Integer page, Integer pageSize) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        List<User> users = userMapper.selectList(wrapper);
        return users.stream().map(this::copyUserToUserVO).collect(Collectors.toList());
    }

    private UserVO copyUserToUserVO(User user) {
        return UserVO.builder()
                .username(user.getUsername())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }
}
