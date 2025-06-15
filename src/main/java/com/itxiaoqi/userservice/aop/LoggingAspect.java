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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Aspect
@Component
public class LoggingAspect {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private UserMapper userMapper;

    @Pointcut("@annotation(com.itxiaoqi.userservice.anno.Loggable)")
    public void logPointcut() {}

    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String ip=getClientIp(request);
        String operation = signature.getMethod().getAnnotation(Loggable.class).value();
        Long userId= UserIdContext.getId();
        if(userId==null) userId=1L;
        String params= JSONUtil.toJsonStr(joinPoint.getArgs());
        OperationLog operationLog = OperationLog.builder()
                .operatorId(userId)
                .operation(operation)
                .ip(ip)
                .requestParams(params)
                .createTime(LocalDateTime.now())
                .build();
        try {
            boolean flag = signature.getMethod().getAnnotation(Loggable.class).flag();
            Map<String,Object> mp=new HashMap<>();
            if(flag){
                mp.put("old",userMapper.selectById(userId));
            }
            Object result = joinPoint.proceed();
            if(flag){
                mp.put("new",userMapper.selectById(userId));
            }
            saveLog(operationLog,true,mp.toString(),"");
            return result;
        } catch (Exception e) {
            saveLog(operationLog,false,"",e.getMessage());
            throw e;
        }
    }

    private void saveLog(OperationLog operationLog,Boolean operationStatus,String detail,String errorMessage){
        operationLog.setOperationStatus(operationStatus);
        operationLog.setDetail(detail);
        operationLog.setErrorMessage(errorMessage);
        rabbitTemplate.convertAndSend(Constant.LOGGING_EXCHANGE,
                Constant.LOGGING_ROUTING,
                operationLog);
    }

    public String getClientIp(HttpServletRequest request) {
        String ipAddress = null;

        ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress != null && !ipAddress.isEmpty() && !"unknown".equalsIgnoreCase(ipAddress)) {
            // 如果 X-Forwarded-For 包含多个 IP 地址，取第一个非 unknown 的 IP
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