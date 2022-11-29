package com.heng.crm.workbench.service;

import com.heng.crm.workbench.domain.Customer;

import java.util.List;

public interface CustomerService {
    //根据客户名模糊查询可能的客户名
    List<String> queryCustomerNameByName(String name);

    //根据客户名查询客户
    Customer queryCustomerByName(String name);
}
