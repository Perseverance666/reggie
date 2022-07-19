package com.example.reggie.interceptor;

import com.alibaba.fastjson.JSON;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Date: 2022/6/7 21:37
 */

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求URL
        String requestURI = request.getRequestURI();
        log.info("拦截到uri："+requestURI);
        //1-1如果用户已登录，放行（网页端）
        if((request.getSession().getAttribute("employee")) != null){
            Long empId = (Long) request.getSession().getAttribute("employee");

            //将当前用户id设置到ThreadLocal中
            BaseContext.setCurrentId(empId);
            log.info("用户已登录，用户id为"+empId);
            return true;
        }
        //1-2如果用户已登录，放行（移动端）
        if((request.getSession().getAttribute("user")) != null){
            Long userId = (Long) request.getSession().getAttribute("user");

            //将当前用户id设置到ThreadLocal中
            BaseContext.setCurrentId(userId);
            log.info("用户已登录，用户id为"+userId);
            return true;
        }

        //2.。用户未登录，拦截
        //request.js中要求是NOTLOGIN
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return false;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
