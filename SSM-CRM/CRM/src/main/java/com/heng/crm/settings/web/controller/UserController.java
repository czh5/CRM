package com.heng.crm.settings.web.controller;

import com.heng.crm.commons.constants.Constant;
import com.heng.crm.commons.domain.ReturnObject;
import com.heng.crm.commons.utils.DateUtils;
import com.heng.crm.settings.domain.User;
import com.heng.crm.settings.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Resource
    private UserService userService;

    /**
     * url应为 Controller方法的处理结果响应的页面的资源路径
     */
    @RequestMapping("/settings/qx/user/toLogin.do")
    public String toLogin() {
        return "settings/qx/user/login";
    }

    /**
     * 登陆功能
     */
    @RequestMapping("/settings/qx/user/login.do")
    @ResponseBody
    public Object login(String loginAct, String loginPwd, String isRemPwd,
                        HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        //封装数据
        Map<String,Object> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        //调用service层方法查询用户
        User user = userService.queryUserByLoginActAndPwd(map);

        //验证用户信息
        ReturnObject ro = new ReturnObject();
        if (user == null) {
            //登陆失败，账号或密码错误
            ro.setCode(Constant.RETURN_OBJECT_CODE_FAILURE);
            ro.setMessage("账号或密码错误");
        } else {
            if (DateUtils.formatDatetime(new Date()).compareTo(user.getExpireTime()) > 0) {
                //登陆失败，账号已过期
                ro.setCode(Constant.RETURN_OBJECT_CODE_FAILURE);
                ro.setMessage("账号已过期");
            } else if ("0".equals(user.getLockState())) {
                //登陆失败，用户状态被锁定
                ro.setCode(Constant.RETURN_OBJECT_CODE_FAILURE);
                ro.setMessage("用户状态被锁定");
            } else if (!user.getAllowIps().contains(request.getRemoteAddr())) {
                //登陆失败，ip受限
                ro.setCode(Constant.RETURN_OBJECT_CODE_FAILURE);
                ro.setMessage("ip受限");
            } else {
                //登陆成功
                ro.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                //保存用户信息
                session.setAttribute(Constant.SESSION_USER,user);
                //实现十天免登陆
                if ("true".equals(isRemPwd)) {
                    //如果勾选了，就添加cookie
                    Cookie c1 = new Cookie("loginAct",loginAct);
                    c1.setMaxAge(10*24*60*60);
                    response.addCookie(c1);
                    Cookie c2 = new Cookie("loginPwd",loginPwd);
                    c2.setMaxAge(10*24*60*60);
                    response.addCookie(c2);
                } else {
                    //如果没勾选了，就删除cookie
                    Cookie c1 = new Cookie("loginAct","1");
                    c1.setMaxAge(0);
                    response.addCookie(c1);
                    Cookie c2 = new Cookie("loginPwd","1");
                    c2.setMaxAge(0);
                    response.addCookie(c2);
                }
            }
        }

        return ro;
    }

    /**
     * 安全退出
     */
    @RequestMapping("/settings/qx/user/logout.do")
    public String logout(HttpServletResponse response,HttpSession session) {
        //删除cookie
        Cookie c1 = new Cookie("loginAct","1");
        c1.setMaxAge(0);
        response.addCookie(c1);
        Cookie c2 = new Cookie("loginPwd","1");
        c2.setMaxAge(0);
        response.addCookie(c2);
        //销毁session
        session.invalidate();

        //若使用请求转发，地址栏会显示logout.do，用户体验不好，因此用重定向
        return "redirect:/";
    }
}
