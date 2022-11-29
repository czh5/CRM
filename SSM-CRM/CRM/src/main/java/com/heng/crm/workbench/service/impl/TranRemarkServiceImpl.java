package com.heng.crm.workbench.service.impl;

import com.heng.crm.workbench.dao.TranRemarkMapper;
import com.heng.crm.workbench.domain.TranRemark;
import com.heng.crm.workbench.service.TranRemarkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TranRemarkServiceImpl implements TranRemarkService {

    @Resource
    private TranRemarkMapper tranRemarkMapper;

    @Override
    public List<TranRemark> queryTranRemarkForDetailByTranId(String tranId) {
        return tranRemarkMapper.selectTranRemarkForDetailByTranId(tranId);
    }
}
