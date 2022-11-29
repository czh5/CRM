package com.heng.crm.workbench.web.controller;

import com.heng.crm.commons.constants.Constant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class UserIndexController {

    /**
     * 登录后跳转至首页
     */
    @RequestMapping("/workbench/index.do")
    public String index() {
        return "workbench/index";
    }

    /**
     * 获取用户信息
     */
    @RequestMapping("/workbench/getUserInf.do")
    @ResponseBody
    public Object getUserInf(HttpSession session) {
        return session.getAttribute(Constant.SESSION_USER);
    }
}
