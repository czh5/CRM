package com.heng.crm.settings.service;

import com.heng.crm.settings.domain.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    //根据用户名和密码查询用户
    User queryUserByLoginActAndPwd(Map<String,Object> map);

    //查询所有用户
    List<User> queryAllUser();
}
