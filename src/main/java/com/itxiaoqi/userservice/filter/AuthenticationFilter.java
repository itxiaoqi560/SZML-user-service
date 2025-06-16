package com.itxiaoqi.userservice.filter;

import com.itxiaoqi.userservice.cache.RedisCache;
import com.itxiaoqi.userservice.constant.Constant;
import com.itxiaoqi.userservice.constant.ExceptionConstant;
import com.itxiaoqi.userservice.constant.RedisConstant;
import com.itxiaoqi.userservice.context.UserIdContext;
import com.itxiaoqi.userservice.entity.po.User;
import com.itxiaoqi.userservice.exception.BusinessException;
import com.itxiaoqi.userservice.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private RedisCache redisCache;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取要放开的接口路径
        String exclusions = getFilterConfig().getInitParameter("exclusions");
        //获取本次访问的接口路径
        String requestURI = request.getRequestURI();
        log.info("访问接口：{}",requestURI);
        //判断是否为可放行路径
        if (isExcludedPath(requestURI, exclusions)) {
            //是，放行
            filterChain.doFilter(request, response);
            return;
        }
        //获取token
        String token = request.getHeader(Constant.TOKEN);
        //解析token
        Long userId;
        try {
            Claims claims = JwtUtil.parseJWT(Constant.SECRET_KEY,token);
            userId = Long.valueOf(claims.get(Constant.ID).toString());
        } catch (Exception e) {
            log.error("jwt令牌解析失败");
            throw new BusinessException(ExceptionConstant.TOKEN_PARSING_FAILED);
        }
        //从redis中获取用户信息
        String loginKey = RedisConstant.LOGIN_KEY + userId;
        User user = redisCache.get(loginKey, User.class);
        //判断获取到的用户信息是否为空，因为redis里面可能并不存在这个用户信息，例如缓存过期了
        if(Objects.isNull(user)){
            log.error("用户还未登录");
            //抛出一个异常
            throw new BusinessException(ExceptionConstant.USER_NOT_LOGGED_IN);
        }
        UserIdContext.setId(userId);
        //全部做完之后，就放行
        filterChain.doFilter(request, response);
        UserIdContext.removeId();
    }

    private boolean isExcludedPath(String requestURI, String exclusions) {
        if (!StringUtils.hasText(exclusions)) {
            return false;
        }

        String[] excludedPaths = exclusions.split(",");
        for (String path : excludedPaths) {
            if (pathMatcher.match(path.trim(), requestURI)) {
                return true;
            }
        }
        return false;
    }

}