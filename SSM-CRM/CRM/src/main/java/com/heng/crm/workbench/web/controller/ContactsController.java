package com.heng.crm.workbench.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ContactsController {

    /**
     * 去往联系人页面
     * @return
     */
    @RequestMapping("/workbench/contacts/toContacts.do")
    public String toContacts() {
        return "workbench/contacts/index";
    }
}
