package com.heng.crm.workbench.service;

import com.heng.crm.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

public interface ClueService {

    //根据条件分页查询线索
    List<Clue> queryClueByConditionForPage(Map<String, Object> map);

    //根据条件查询线索总条数
    int queryCountOfCLueByCondition(Map<String, Object> map);

    //保存创建的线索
    int saveCreateClue(Clue clue);

    //根据id查询线索明细
    Clue queryClueForDetailById(String id);

    //保存线索转换
    void saveConvertClue(Map<String, Object> map);
}
