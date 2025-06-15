package com.itxiaoqi.userservice.controller;

import com.itxiaoqi.userservice.anno.Loggable;
import com.itxiaoqi.userservice.entity.dto.UserDTO;
import com.itxiaoqi.userservice.entity.result.Result;
import com.itxiaoqi.userservice.entity.vo.UserVO;
import com.itxiaoqi.userservice.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userDTO
     * @return
     */
    @PostMapping("/user/register")
    public Result register(@RequestBody UserDTO userDTO) {
        return Result.error("功能还未实现");
    }

    /**
     * 登录生成jwtToken
     *
     * @param userDTO
     * @return
     */
    @PostMapping("/user/login")
    public Result login(@RequestBody UserDTO userDTO) {
        return Result.error("功能还未实现");
    }

    /**
     * 分页用户列表
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/users")
    @Loggable(value = "getUsers")
    public Result getUsers(@RequestParam(required = false, defaultValue = "1") Integer page,
                           @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        List<UserVO> userList = userService.getUsers(page, pageSize);
        return Result.success(userList);
    }


    /**
     * 查询用户信息
     *
     * @param id
     * @param userDTO
     * @return
     */
    @GetMapping("/user/{id}")
    public Result getUserById(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return Result.error("功能还未实现");
    }

    /**
     * 修改用户信息
     *
     * @param id
     * @return
     */
    @PutMapping("/user/{id}")
    public Result updateInfo(@PathVariable Long id) {
        return Result.error("功能还未实现");
    }

    /**
     * 密码重置
     *
     * @param idList
     * @return
     */
    @PostMapping("/user/reset-password")
    public Result resetPassword(@RequestParam List<Long> idList) {
        return Result.error("功能还未实现");
    }
}
