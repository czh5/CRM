package com.heng.crm.workbench.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    /**
     * 跳转至main的首页，即工作台
     * @return
     */
    @RequestMapping("/workbench/main/index.do")
    public String index() {
        return "/workbench/main/index";
    }
}
