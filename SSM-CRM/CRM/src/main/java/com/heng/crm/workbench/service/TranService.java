package com.heng.crm.workbench.service;

import com.heng.crm.workbench.domain.FunnelOV;
import com.heng.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranService {

    //根据条件分页查询交易记录
    List<Tran> queryTranByConditionForPage(Map<String, Object> map);

    //根据条件查询交易记录总条数
    int queryCountOfTranByCondition(Map<String, Object> map);

    //添加创建的交易记录
    void saveCreateTran(Map<String, Object> map);

    //根据id查询交易明细
    Tran queryTranForDetailById(String id);

    //根据阶段分组查询交易数量
    List<FunnelOV> queryCountOfTranGroupByStage();
}
