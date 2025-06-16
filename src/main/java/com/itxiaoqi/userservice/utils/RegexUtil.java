package com.itxiaoqi.userservice.utils;


import com.itxiaoqi.userservice.constant.ExceptionConstant;
import com.itxiaoqi.userservice.constant.RegexPatterns;
import com.itxiaoqi.userservice.exception.BusinessException;

import java.util.Objects;

public class RegexUtil {
    /**
     * 验证手机号格式是否合法
     * @param phoneNumber 待校验的手机号
     */
    public static void isPhoneNumberValid(String phoneNumber){
        if(mismatch(phoneNumber, RegexPatterns.PHONE_NUMBER_REGEX)){
            throw new BusinessException(ExceptionConstant.PHONE_NUMBER_FORMAT_IS_INCORRECT);
        }
    }

    /**
     * 验证邮箱格式是否合法
     * @param email 待校验的邮箱
     */
    public static void isEmailValid(String email){
        if(mismatch(email,RegexPatterns.EMAIL_REGEX)){
            throw new BusinessException(ExceptionConstant.EMAIL_FORMAT_IS_INCORRECT);
        }
    }

    /**
     * 验证用户名格式是否合法
     * @param username 待验证的用户名
     */
    public static void isUsernameValid(String username) {
        if(mismatch(username,RegexPatterns.USERNAME_REGEX)){
            throw new BusinessException(ExceptionConstant.USERNAME_FORMAT_IS_INCORRECT);
        }
    }

    /**
     * 验证密码格式是否合法
     * @param password 待验证的密码
     */
    public static void isPasswordValid(String password) {
        if(mismatch(password,RegexPatterns.PASSWORD_REGEX)){
            throw new BusinessException(ExceptionConstant.PASSWORD_FORMAT_IS_INCORRECT);
        }
    }


    private static boolean mismatch(String str, String regex){
        if (Objects.isNull(str)) {
            return true;
        }
        return !str.matches(regex);
    }

}