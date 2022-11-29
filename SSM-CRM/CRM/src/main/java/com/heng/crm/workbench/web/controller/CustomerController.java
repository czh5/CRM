package com.heng.crm.workbench.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomerController {

    /**
     * 去往客户页面
     * @return
     */
    @RequestMapping("/workbench/customer/toCustomer.do")
    public String toCustomer() {
        return "workbench/customer/index";
    }
}
