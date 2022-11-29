package com.heng.crm.settings.service;

import com.heng.crm.settings.domain.DicValue;

import java.util.List;

public interface DicValueService {

    //根据类型查询数据字典的值
    List<DicValue> queryDicValueByTypeCode(String typeCode);
}
