package com.heng.crm.workbench.service;

import com.heng.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranHistoryService {
    //根据tranId查询交易历史
    List<TranHistory> queryTranHistoryForDetailByTranId(String tranId);
}
