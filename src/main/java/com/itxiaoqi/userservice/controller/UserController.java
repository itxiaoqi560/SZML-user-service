package com.itxiaoqi.userservice.controller;

import com.itxiaoqi.userservice.anno.Loggable;
import com.itxiaoqi.userservice.context.UserIdContext;
import com.itxiaoqi.userservice.entity.dto.UserDTO;
import com.itxiaoqi.userservice.entity.result.PageResult;
import com.itxiaoqi.userservice.entity.result.Result;
import com.itxiaoqi.userservice.entity.vo.UserVO;
import com.itxiaoqi.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userDTO 用户DTO
     * @return 响应结果
     */
    @PostMapping("/user/register")
    @Loggable(value = "用户注册")
    public Result register(@RequestBody UserDTO userDTO) {
        log.info("用户注册：{}", userDTO);
        userService.register(userDTO);
        return Result.success();
    }

    /**
     * 用户登录
     *
     * @param userDTO 用户DTO
     * @return 响应结果
     */
    @PostMapping("/user/login")
    @Loggable(value = "用户登录")
    public Result login(@RequestBody UserDTO userDTO) {
        log.info("用户登录：{}", userDTO);
        UserVO userVO=userService.login(userDTO);
        return Result.successWithToken(userVO);
    }

    /**
     * 分页用户列表
     *
     * @param page 页码
     * @param pageSize 页大小
     * @return 响应结果
     */
    @GetMapping("/users")
    @Loggable(value = "分页用户列表")
    public Result getUsers(@RequestParam(required = false, defaultValue = "1") Integer page,
                           @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        log.info("分页用户列表：{},{},{}",UserIdContext.getId(),page,pageSize);
        PageResult pageResult = userService.getUsers(page, pageSize);
        return Result.success(pageResult);
    }


    /**
     * 查询用户信息
     *
     * @param id 用户id
     * @return 响应结果
     */
    @GetMapping("/user/{id}")
    @Loggable(value = "查询用户信息")
    public Result getUserById(@PathVariable Long id) {
        log.info("查询用户信息：{},{}",UserIdContext.getId(), id);
        UserVO userVO=userService.getUserById(id);
        return Result.success(userVO);
    }

    /**
     * 修改用户信息
     *
     * @param id 用户id
     * @param userDTO 用户DTO
     * @return 响应结果
     */
    @PutMapping("/user/{id}")
    @Loggable(value = "修改用户信息")
    public Result updateInfo(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        log.info("修改用户信息：{},{},{}", UserIdContext.getId(),id,userDTO);
        userService.updateInfo(id,userDTO);
        return Result.success();
    }

    /**
     * 密码重置
     *
     * @param idList 用户id集合
     * @return 响应结果
     */
    @PostMapping("/user/reset-password")
    @Loggable(value = "密码重置")
    public Result resetPassword(@RequestParam List<Long> idList) {
        log.info("密码重置：{},{}", UserIdContext.getId(),idList);
        userService.resetPassword(idList);
        return Result.success();
    }


    /**
     * 超管调用：升级用户为管理员
     *
     * @param id 用户id
     */
    @PutMapping("/user/upgradeToAdmin")
    @Loggable(value = "超管调用：升级用户为管理员")
    public Result upgradeToAdmin(@RequestParam Long id){
        log.info("超管调用：升级用户为管理员：{},{}", UserIdContext.getId(),id);
        userService.upgradeToAdmin(id);
        return Result.success();
    }

    /**
     * 超管调用：降级用户为普通角色
     *
     * @param id 用户id
     */
    @PutMapping("/user/downgradeToUser")
    @Loggable(value = "超管调用：降级用户为普通角色")
    public Result downgradeToUser(@RequestParam Long id){
        log.info("超管调用：降级用户为普通角色：{},{}", UserIdContext.getId(),id);
        userService.downgradeToUser(id);
        return Result.success();
    }
}
