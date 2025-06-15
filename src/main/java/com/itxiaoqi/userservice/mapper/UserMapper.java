package com.itxiaoqi.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itxiaoqi.userservice.entity.po.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
