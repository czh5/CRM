package com.heng.crm.settings.web.interceptor;

import com.heng.crm.commons.constants.Constant;
import com.heng.crm.settings.domain.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //对每个请求进行拦截，检查是否已登录
        User user = (User) request.getSession().getAttribute(Constant.SESSION_USER);
        if (user == null) {
            //表示未登录，需要重定向到登陆页面
            response.sendRedirect("/CRM/");
            return false;
        }
        return true;
    }
}
