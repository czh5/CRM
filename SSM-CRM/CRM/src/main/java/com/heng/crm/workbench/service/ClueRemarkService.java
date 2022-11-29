package com.heng.crm.workbench.service;

import com.heng.crm.workbench.domain.ClueRemark;

import java.util.List;

public interface ClueRemarkService {
    //根据id查询线索备注
    List<ClueRemark> queryClueRemarkByClueId(String clueId);
}
