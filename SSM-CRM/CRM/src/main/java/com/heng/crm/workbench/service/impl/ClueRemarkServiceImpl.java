package com.heng.crm.workbench.service.impl;

import com.heng.crm.workbench.dao.ClueRemarkMapper;
import com.heng.crm.workbench.domain.ClueRemark;
import com.heng.crm.workbench.service.ClueRemarkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ClueRemarkServiceImpl implements ClueRemarkService {

    @Resource
    private ClueRemarkMapper clueRemarkMapper;

    @Override
    public List<ClueRemark> queryClueRemarkByClueId(String clueId) {
        return clueRemarkMapper.selectClueRemarkForDetailByClueId(clueId);
    }
}
