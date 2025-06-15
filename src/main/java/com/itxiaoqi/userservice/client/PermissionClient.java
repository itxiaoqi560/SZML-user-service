package com.itxiaoqi.userservice.client;

import com.itxiaoqi.userservice.entity.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("permission-service")
public interface PermissionClient {
    /**
     * 绑定默认角色（普通用户）
     *
     * @param userId
     */
    @PostMapping("/permission/bindDefaultRole")
    Result bindDefaultRole(@RequestParam Long userId);


    /**
     * 查询用户角色码（返回role_code）
     *
     * @param userId
     * @return
     */
    @GetMapping("/permission/getUserRoleCode")
    Result getUserRoleCode(@RequestParam Long userId);

    /**
     * 超管调用：升级用户为管理员
     *
     * @param userId
     */
    @PutMapping("/permission/upgradeToAdmin")
    Result upgradeToAdmin(@RequestParam Long userId);

    /**
     * 超管调用：降级用户为普通角色
     *
     * @param userId
     */
    @PutMapping("/permission/downgradeToUser")
    Result downgradeToUser(@RequestParam Long userId);
}

