package com.heng.crm.workbench.service;

import com.heng.crm.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationService {
    //添加线索关联的市场活动
    int saveClueActivityRelationByList(List<ClueActivityRelation> list);

    //根据clueId和activityId删除线索和市场活动的关联关系
    int deleteClueActivityRelationByClueIdAndActivityId(ClueActivityRelation relation);
}
