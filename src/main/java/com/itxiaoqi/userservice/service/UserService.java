package com.itxiaoqi.userservice.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.itxiaoqi.userservice.entity.dto.UserDTO;
import com.itxiaoqi.userservice.entity.po.User;
import com.itxiaoqi.userservice.entity.result.PageResult;
import com.itxiaoqi.userservice.entity.vo.UserVO;

import java.util.List;

public interface UserService extends IService<User> {

    /**
     * 分页用户列表
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    PageResult getUsers(Integer page, Integer pageSize);

    /**
     * 用户注册
     * @param userDTO 用户DTO
     */
    void register(UserDTO userDTO);

    /**
     * 用户登录
     * @param userDTO 用户DTO
     * @return 用户VO
     */
    UserVO login(UserDTO userDTO);

    /**
     * 查询用户信息
     * @param id 用户id
     * @return 用户VO
     */
    UserVO getUserById(Long id);

    /**
     * 修改用户信息
     * @param id 用户id
     * @param userDTO 用户DTO
     */
    void updateInfo(Long id, UserDTO userDTO);

    /**
     * 密码重置
     * @param idList 用户id集合
     */
    void resetPassword(List<Long> idList);

    /**
     * 超管调用：降级用户为普通角色
     * @param id 用户id
     */
    void downgradeToUser(Long id);

    /**
     * 超管调用：升级用户为管理员
     * @param id 用户id
     */
    void upgradeToAdmin(Long id);
}
