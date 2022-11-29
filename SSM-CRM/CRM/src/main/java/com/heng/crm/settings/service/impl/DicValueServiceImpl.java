package com.heng.crm.settings.service.impl;

import com.heng.crm.settings.dao.DicValueMapper;
import com.heng.crm.settings.domain.DicValue;
import com.heng.crm.settings.service.DicValueService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DicValueServiceImpl implements DicValueService {

    @Resource
    private DicValueMapper dicValueMapper;

    @Override
    public List<DicValue> queryDicValueByTypeCode(String typeCode) {
        return dicValueMapper.selectDivValueByTypeCode(typeCode);
    }
}
