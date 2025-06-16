package com.itxiaoqi.userservice.aop;

import cn.hutool.json.JSONUtil;
import com.itxiaoqi.userservice.anno.Loggable;
import com.itxiaoqi.userservice.constant.Constant;
import com.itxiaoqi.userservice.context.UserIdContext;
import com.itxiaoqi.userservice.entity.po.OperationLog;
import com.itxiaoqi.userservice.mapper.UserMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;

@Aspect
@Component
public class LoggingAspect {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Pointcut("@annotation(com.itxiaoqi.userservice.anno.Loggable)")
    public void logPointcut() {}

    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取ip地址
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String ip=getClientIp(request);
        //获取方法执行的方法
        String operation = signature.getMethod().getAnnotation(Loggable.class).value();
        //获取操作者id
        Long userId= UserIdContext.getId();
        //序列化方法参数
        String params= JSONUtil.toJsonStr(joinPoint.getArgs());
        //封装日志信息
        OperationLog operationLog = OperationLog.builder()
                .operatorId(userId)
                .operation(operation)
                .ip(ip)
                .requestParams(params)
                .createTime(LocalDateTime.now())
                .build();
        try {
            //执行原生方法
            Object result = joinPoint.proceed();
            //消息队列持久化日志
            saveLog(operationLog,true,"");
            return result;
        } catch (Exception e) {
            //消息队列持久化日志
            saveLog(operationLog,false,e.getMessage());
            throw e;
        }
    }

    /**
     * 持久化日志
     * @param operationLog 操作日志
     * @param operationStatus 操作状态
     * @param errorMessage 错误信息
     */

    private void saveLog(OperationLog operationLog,Boolean operationStatus,String errorMessage){
        //设置操作状态
        operationLog.setOperationStatus(operationStatus);
        //设置错误信息
        operationLog.setErrorMessage(errorMessage);
        //消息队列持久化日志
        rabbitTemplate.convertAndSend(Constant.LOGGING_EXCHANGE,
                Constant.LOGGING_ROUTING,
                operationLog);
    }

    /**
     * AI-Deepseek
     * @param request http请求
     * @return ip地址
     */
    public String getClientIp(HttpServletRequest request) {
        String ipAddress = null;

        ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress != null && !ipAddress.isEmpty() && !"unknown".equalsIgnoreCase(ipAddress)) {
            int index = ipAddress.indexOf(',');
            if (index != -1) {
                ipAddress = ipAddress.substring(0, index).trim();
            }
            return ipAddress;
        }

        ipAddress = request.getHeader("Proxy-Client-IP");
        if (ipAddress != null && !ipAddress.isEmpty() && !"unknown".equalsIgnoreCase(ipAddress)) {
            return ipAddress;
        }

        ipAddress = request.getHeader("WL-Proxy-Client-IP");
        if (ipAddress != null && !ipAddress.isEmpty() && !"unknown".equalsIgnoreCase(ipAddress)) {
            return ipAddress;
        }

        ipAddress = request.getHeader("HTTP_CLIENT_IP");
        if (ipAddress != null && !ipAddress.isEmpty() && !"unknown".equalsIgnoreCase(ipAddress)) {
            return ipAddress;
        }

        ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (ipAddress != null && !ipAddress.isEmpty() && !"unknown".equalsIgnoreCase(ipAddress)) {
            return ipAddress;
        }

        ipAddress = request.getHeader("X-Real-IP");
        if (ipAddress != null && !ipAddress.isEmpty() && !"unknown".equalsIgnoreCase(ipAddress)) {
            return ipAddress;
        }

        ipAddress = request.getRemoteAddr();

        if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
            return "127.0.0.1";
        }

        return ipAddress;
    }

}