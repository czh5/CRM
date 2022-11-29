package com.heng.crm.workbench.service.impl;

import com.heng.crm.commons.constants.Constant;
import com.heng.crm.commons.utils.DateUtils;
import com.heng.crm.commons.utils.UUIDUtils;
import com.heng.crm.settings.domain.User;
import com.heng.crm.workbench.dao.CustomerMapper;
import com.heng.crm.workbench.dao.TranHistoryMapper;
import com.heng.crm.workbench.dao.TranMapper;
import com.heng.crm.workbench.domain.Customer;
import com.heng.crm.workbench.domain.FunnelOV;
import com.heng.crm.workbench.domain.Tran;
import com.heng.crm.workbench.domain.TranHistory;
import com.heng.crm.workbench.service.TranService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TranServiceImpl implements TranService {

    @Resource
    private TranMapper tranMapper;

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private TranHistoryMapper tranHistoryMapper;

    @Override
    public List<Tran> queryTranByConditionForPage(Map<String, Object> map) {
        return tranMapper.selectTranByConditionForPage(map);
    }

    @Override
    public int queryCountOfTranByCondition(Map<String, Object> map) {
        return tranMapper.selectCountOfTranByCondition(map);
    }

    @Override
    public void saveCreateTran(Map<String, Object> map) {
        String customerName = (String) map.get("customerName");
        User user = (User) map.get(Constant.SESSION_USER);

        //查询该客户名对应的客户
        Customer customer = customerMapper.selectCustomerByName(customerName);
        if (customer == null) {
            //没有该用户则新建
            customer = new Customer();
            customer.setOwner(user.getId());
            customer.setName(customerName);
            customer.setId(UUIDUtils.getUUID());
            customer.setCreateTime(DateUtils.formatDatetime(new Date()));
            customer.setCreateBy(user.getId());
            customerMapper.insertCustomer(customer);
        }

        //创建交易
        Tran tran = new Tran();
        tran.setStage((String) map.get("stage"));
        tran.setOwner((String) map.get("owner"));
        tran.setName((String) map.get("name"));
        tran.setMoney((String) map.get("money"));
        tran.setId(UUIDUtils.getUUID());
        tran.setExpectedDate((String) map.get("expectedDate"));
        tran.setCustomerId(customer.getId());
        tran.setCreateTime(DateUtils.formatDatetime(new Date()));
        tran.setCreateBy(user.getId());
        tran.setContactsId((String) map.get("contactsId"));
        tran.setActivityId((String) map.get("activityId"));
        tran.setContactSummary((String) map.get("contactSummary"));
        tran.setDescription((String) map.get("description"));
        tran.setNextContactTime((String) map.get("nextContactTime"));
        tran.setSource((String) map.get("source"));
        tran.setType((String) map.get("type"));
        tranMapper.insertTran(tran);

        //创建交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setCreateBy(user.getId());
        tranHistory.setCreateTime(DateUtils.formatDatetime(new Date()));
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setId(UUIDUtils.getUUID());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setStage(tran.getStage());
        tranHistory.setTranId(tran.getId());
        tranHistoryMapper.insertTranHistory(tranHistory);
    }

    @Override
    public Tran queryTranForDetailById(String id) {
        return tranMapper.selectTranForDetailById(id);
    }

    @Override
    public List<FunnelOV> queryCountOfTranGroupByStage() {
        return tranMapper.selectCountOfTranGroupByStage();
    }
}
