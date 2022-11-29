package com.heng.crm.workbench.service;

import com.heng.crm.workbench.domain.TranRemark;

import java.util.List;

public interface TranRemarkService {

    //根据tranId查询备注
    List<TranRemark> queryTranRemarkForDetailByTranId(String tranId);
}
