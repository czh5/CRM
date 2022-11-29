package com.heng.crm.workbench.service.impl;

import com.heng.crm.workbench.dao.TranHistoryMapper;
import com.heng.crm.workbench.domain.TranHistory;
import com.heng.crm.workbench.service.TranHistoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TranHistoryServiceImpl implements TranHistoryService {

    @Resource
    private TranHistoryMapper tranHistoryMapper;

    @Override
    public List<TranHistory> queryTranHistoryForDetailByTranId(String tranId) {
        return tranHistoryMapper.selectTranHistoryForDetailByTranId(tranId);
    }
}
