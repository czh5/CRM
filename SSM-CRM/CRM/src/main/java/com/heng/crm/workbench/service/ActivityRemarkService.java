package com.heng.crm.workbench.service;

import com.heng.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkService {

    //根据市场活动id查询市场活动备注明细
    List<ActivityRemark> queryActivityRemarkForDetailByActivityId(String activityId);

    //添加创建的市场活动备注
    int saveCreateActivityRemark(ActivityRemark remark);

    //根据id删除市场活动备注
    int deleteActivityRemarkById(String id);

    //修改市场活动备注
    int saveEditActivityRemark(ActivityRemark remark);
}
