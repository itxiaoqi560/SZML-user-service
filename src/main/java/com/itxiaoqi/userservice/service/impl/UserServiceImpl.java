package com.itxiaoqi.userservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itxiaoqi.userservice.cache.RedisCache;
import com.itxiaoqi.userservice.client.PermissionClient;
import com.itxiaoqi.userservice.constant.Constant;
import com.itxiaoqi.userservice.constant.ExceptionConstant;
import com.itxiaoqi.userservice.constant.RedisConstant;
import com.itxiaoqi.userservice.context.UserIdContext;
import com.itxiaoqi.userservice.entity.dto.UserDTO;
import com.itxiaoqi.userservice.entity.po.User;
import com.itxiaoqi.userservice.entity.result.PageResult;
import com.itxiaoqi.userservice.entity.result.Result;
import com.itxiaoqi.userservice.entity.vo.UserVO;
import com.itxiaoqi.userservice.exception.BusinessException;
import com.itxiaoqi.userservice.generator.SnowflakeIdGenerator;
import com.itxiaoqi.userservice.mapper.UserMapper;
import com.itxiaoqi.userservice.service.UserService;
import com.itxiaoqi.userservice.utils.RegexUtil;
import io.jsonwebtoken.lang.Strings;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;
    private final SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);
    @Resource
    private PermissionClient permissionClient;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private RedisCache redisCache;

    /**
     * 分页用户列表
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    @Override
    public PageResult getUsers(Integer page, Integer pageSize) {
        //查询所有的用户
        List<User> userList = userMapper.selectList(lambdaQuery().orderBy(true, true, User::getCreateTime).getWrapper());
        //获取执行操作者的角色码
        String userRoleCode = permissionClient.getUserRoleCode(UserIdContext.getId());
        //过滤出可以访问的用户
        userList = userList.stream()
                .filter(user -> UserIdContext.getId().equals(user.getId()) || permissionGe(userRoleCode, user.getId()))
                .collect(Collectors.toList());
        //查询执行的分页用户
        Integer beginSize = (page - 1) * pageSize;
        int total = userList.size();
        List<User> resultUserList = new ArrayList<>();
        for (int i = beginSize; i < beginSize + pageSize && i < total; ++i) {
            resultUserList.add(userList.get(i));
        }
        //将User转换为UserVO
        List<UserVO> userVOList = resultUserList.stream()
                .map(this::convertUserToUserVO)
                .collect(Collectors.toList());
        return new PageResult<>((long) total, userVOList);
    }

    /**
     * 用户注册
     * @param userDTO 用户DTO
     */
    @Override
    @GlobalTransactional
    public void register(UserDTO userDTO) {
        //校验密码格式
        RegexUtil.isPasswordValid(userDTO.getPassword());
        //校验邮箱格式
        RegexUtil.isEmailValid(userDTO.getEmail());
        //校验手机号格式
        RegexUtil.isPhoneNumberValid(userDTO.getPhone());
        //校验用户名格式
        RegexUtil.isUsernameValid(userDTO.getUsername());
        //根据用户名查找用户
        User user = userMapper.selectOne(lambdaQuery().eq(User::getUsername, userDTO.getUsername()).getWrapper());
        //判断用户名是否唯一
        if (!Objects.isNull(user)) {
            throw new BusinessException(ExceptionConstant.USERNAME_ALREADY_EXIST);
        }
        //根据手机号查找用户
        user = userMapper.selectOne(lambdaQuery().eq(User::getPhone, userDTO.getPhone()).getWrapper());
        //判断手机号是否唯一
        if (!Objects.isNull(user)) {
            throw new BusinessException(ExceptionConstant.PHONE_NUMBER_ALREADY_REGISTERED);
        }
        //封装用户信息
        user = User.builder()
                .id(idGenerator.nextId())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .phone(userDTO.getPhone())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .createTime(LocalDateTime.now())
                .build();
        //用户信息落库
        userMapper.insert(user);
        //用户角色绑定
        permissionClient.bindDefaultRole(user.getId());
    }

    /**
     * 用户登录
     * @param userDTO 用户DTO
     * @return 用户VO
     */
    @Override
    public UserVO login(UserDTO userDTO) {
        //校验用户名格式
        RegexUtil.isUsernameValid(userDTO.getUsername());
        //校验密码格式
        RegexUtil.isPasswordValid(userDTO.getPassword());
        //根据用户名查找用户
        User user = userMapper.selectOne(lambdaQuery().eq(User::getUsername, userDTO.getUsername()).getWrapper());
        //判断用户是否存在，若存在，判断密码是否匹配
        if (Objects.isNull(user)||!passwordEncoder.matches(userDTO.getPassword(),user.getPassword())) {
            throw new BusinessException(ExceptionConstant.USERNAME_OR_PASSWORD_ERROR);
        }
        //保存用户id，为创建身份令牌做准备
        UserIdContext.setId(user.getId());
        //缓存用户登录状态
        redisCache.save(RedisConstant.LOGIN_KEY + user.getId(),
                user,
                RedisConstant.TOKEN_EXPIRE,
                TimeUnit.MILLISECONDS);
        return convertUserToUserVO(user);
    }

    /**
     * 查询用户信息
     * @param id 用户id
     * @return 用户VO
     */
    @Override
    public UserVO getUserById(Long id) {
        //判断要查询的用户是否为自己
        if (id.equals(UserIdContext.getId())) {
            //查询的用户信息是自己的，直接返回
            User user = userMapper.selectById(id);
            if (Objects.isNull(user)) {
                throw new BusinessException(ExceptionConstant.USER_NOT_FOUND);
            }
            return convertUserToUserVO(user);
        }
        //获取自己的角色码
        String userRoleCode = permissionClient.getUserRoleCode(UserIdContext.getId());
        //判断是否有权限操作其他用户id
        hasPermission(userRoleCode, id);
        //有权限，查找用户信息
        User user = userMapper.selectById(id);
        if (Objects.isNull(user)) {
            throw new BusinessException(ExceptionConstant.USER_NOT_FOUND);
        }
        return convertUserToUserVO(user);
    }

    /**
     * 修改用户信息
     * @param id 用户id
     * @param userDTO 用户DTO
     */
    @Override
    public void updateInfo(Long id, UserDTO userDTO) {
        //判断要修改的邮箱格式是否正确
        if (Strings.hasText(userDTO.getEmail())) {
            RegexUtil.isEmailValid(userDTO.getEmail());
        }
        //判断要修改的密码格式是否正确
        if (Strings.hasText(userDTO.getPassword())) {
            RegexUtil.isPasswordValid(userDTO.getPassword());
        }
        //判断要修改的手机号格式是否正确
        if (Strings.hasText(userDTO.getPhone())) {
            RegexUtil.isPhoneNumberValid(userDTO.getPhone());
        }
        //判断要修改的用户名格式是否正确
        if (Strings.hasText(userDTO.getUsername())) {
            RegexUtil.isUsernameValid(userDTO.getUsername());
        }
        //判断要修改的用户信息是否是自己的
        if (UserIdContext.getId().equals(id)) {
            updateUserInfo(id, userDTO);
            return;
        }
        //获取自己的角色码
        String userRoleCode = permissionClient.getUserRoleCode(UserIdContext.getId());
        //判断是否有权限修改用户信息
        hasPermission(userRoleCode, id);
        //有权限，修改用户信息
        updateUserInfo(id, userDTO);
    }

    /**
     * 密码重置
     * @param idList 用户id集合
     */
    @Override
    public void resetPassword(List<Long> idList) {
        //获取自己的身份码
        String userRoleCode = permissionClient.getUserRoleCode(UserIdContext.getId());
        //过滤出有权限重置密码的用户
        idList = idList.stream().filter(id -> UserIdContext.getId().equals(id) || permissionGe(userRoleCode, id))
                .collect(Collectors.toList());
        //过滤后为空，直接返回
        if (CollUtil.isEmpty(idList)) {
            return;
        }
        //不为空，批量重置密码
        userMapper.update(lambdaUpdate().set(User::getPassword, passwordEncoder.encode(Constant.DEFAULT_PASSWORD))
                .in(User::getId, idList)
                .getWrapper());
    }

    /**
     * 超管调用：降级用户为普通角色
     * @param id 用户id
     */
    @Override
    public void downgradeToUser(Long id) {
        //判断该用户是否为超管
        String userRoleCode=permissionClient.getUserRoleCode(UserIdContext.getId());
        if(!Constant.SUPER_ADMIN.equals(userRoleCode)){
            throw new BusinessException(ExceptionConstant.PERMISSION_DENIED);
        }
        //降级用户为普通角色
        permissionClient.downgradeToUser(id);
    }

    /**
     * 超管调用：升级用户为管理员
     * @param id 用户id
     */
    @Override
    public void upgradeToAdmin(Long id) {
        //判断该用户是否为超管
        String userRoleCode=permissionClient.getUserRoleCode(UserIdContext.getId());
        if(!Constant.SUPER_ADMIN.equals(userRoleCode)){
            throw new BusinessException(ExceptionConstant.PERMISSION_DENIED);
        }
        //升级用户为管理员
        permissionClient.upgradeToAdmin(id);
    }


    /**
     * 根据用户id更新信息
     * @param id 用户id
     * @param userDTO 用户DTO
     */
    private void updateUserInfo(Long id, UserDTO userDTO) {
        //根据id更新用户信息
        userMapper.update(lambdaUpdate().eq(User::getId, id)
                .set(Strings.hasText(userDTO.getEmail()), User::getEmail, userDTO.getEmail())
                .set(Strings.hasText(userDTO.getPassword()), User::getPassword, passwordEncoder.encode(userDTO.getPassword()))
                .set(Strings.hasText(userDTO.getPhone()), User::getPhone, userDTO.getPhone())
                .set(Strings.hasText(userDTO.getUsername()), User::getUsername, userDTO.getUsername())
                .getWrapper());
    }


    /**
     * 判断用户是否有权限操作id，无权限直接抛出异常
     * @param operatorRoleCode 角色码
     * @param userId 用户id
     */
    private void hasPermission(String operatorRoleCode, Long userId) {
        //若用户为超管，直接返回
        if (operatorRoleCode.equals(Constant.SUPER_ADMIN)) {
            return;
        }
        //若用户为管理员
        if (operatorRoleCode.equals(Constant.ADMIN)) {
            //被操作的用户为普通用户，直接返回
            String userRoleCode = permissionClient.getUserRoleCode(userId);
            if (userRoleCode.equals(Constant.USER)) {
                return;
            }
        }
        //无权限，抛出异常
        throw new BusinessException(ExceptionConstant.PERMISSION_DENIED);
    }

    /**
     * 判断用户权限是否大于用户id的权限
     * @param operatorRoleCode 角色码
     * @param userId 用户id
     * @return 布尔值
     */
    private boolean permissionGe(String operatorRoleCode,Long userId){
        //判断是否有权限，返回布尔值
        try{
            hasPermission(operatorRoleCode,userId);
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 将User转换为UserVO
     * @param user 用户
     * @return 用户VO
     */
    private UserVO convertUserToUserVO(User user) {
        //转换User为UserVO
        return UserVO.builder()
                .username(user.getUsername())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }
}
