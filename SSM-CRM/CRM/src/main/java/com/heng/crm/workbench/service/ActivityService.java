package com.heng.crm.workbench.service;

import com.heng.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    //添加新的市场活动
    int saveCreateActivity(Activity activity);

    //根据条件分页查询市场活动
    List<Activity> queryActivityByConditionForPage(Map<String,Object> map);

    //根据条件查询市场活动总条数
    int queryCountOfActivityByCondition(Map<String,Object> map);

    //根据id数组删除市场活动记录
    int deleteActivityByIds(String[] ids);

    //根据id查询市场活动记录
    Activity queryActivityById(String id);

    //根据id修改市场活动记录
    int saveEditActivity(Activity activity);

    //查询所有市场活动
    List<Activity> queryAllActivity();

    //根据id查询某些市场活动
    List<Activity> querySomeActivityByIds(String[] ids);

    //以list的方式插入多条市场活动
    int saveCreateActivityByList(List<Activity> activityList);

    //根据id查询市场活动明细
    Activity queryActivityForDetailById(String id);

    //根据线索id查询市场活动明细
    List<Activity> queryActivityForDetailByClueId(String clueId);

    //根据活动名模糊查询活动，并根据线索id查询已关联的活动，排除掉这些活动
    List<Activity> queryActivityForDetailByActivityNameAndClueId(Map<String, Object> map);

    //根据市场活动的id数组查询市场活动
    List<Activity> queryActivityForDetailByIds(String[] ids);

    //根据活动名和clueId模糊查询已关联的市场活动
    List<Activity> queryActivityForConvertByActivityNameAndClueId(Map<String, Object> map);
}
