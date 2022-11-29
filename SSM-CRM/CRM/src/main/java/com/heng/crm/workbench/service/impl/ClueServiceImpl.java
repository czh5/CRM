package com.heng.crm.workbench.service.impl;

import com.heng.crm.commons.constants.Constant;
import com.heng.crm.commons.utils.DateUtils;
import com.heng.crm.commons.utils.UUIDUtils;
import com.heng.crm.settings.domain.User;
import com.heng.crm.workbench.dao.*;
import com.heng.crm.workbench.domain.*;
import com.heng.crm.workbench.service.ClueService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ClueServiceImpl implements ClueService {

    @Resource
    private ClueMapper clueMapper;

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private ContactsMapper contactsMapper;

    @Resource
    private ClueRemarkMapper clueRemarkMapper;

    @Resource
    private CustomerRemarkMapper customerRemarkMapper;

    @Resource
    private ContactsRemarkMapper contactsRemarkMapper;

    @Resource
    private ClueActivityRelationMapper clueActivityRelationMapper;

    @Resource
    private ContactsActivityRelationMapper contactsActivityRelationMapper;

    @Resource
    private TranMapper tranMapper;

    @Resource
    private TranRemarkMapper tranRemarkMapper;

    @Override
    public List<Clue> queryClueByConditionForPage(Map<String, Object> map) {
        return clueMapper.selectClueByConditionForPage(map);
    }

    @Override
    public int queryCountOfCLueByCondition(Map<String, Object> map) {
        return clueMapper.selectCountOfClueByCondition(map);
    }

    @Override
    public int saveCreateClue(Clue clue) {
        return clueMapper.insertCreateClue(clue);
    }

    @Override
    public Clue queryClueForDetailById(String id) {
        return clueMapper.selectClueForDetailById(id);
    }

    @Override
    public void saveConvertClue(Map<String, Object> map) {
        //获取clueId
        String clueId = (String) map.get("clueId");
        //获取是否要创建交易
        String isCreateTran = (String) map.get("isCreateTran");

        //1、根据clueId查询线索明细
        Clue clue = clueMapper.selectClueById(clueId);

        //2、把该线索中有关公司的信息提取出来，转移到客户表中
        //封装参数[客户实例]
        Customer cu = new Customer();
        User user = (User) map.get(Constant.SESSION_USER);
        cu.setAddress(clue.getAddress());
        cu.setContactSummary(clue.getContactSummary());
        cu.setCreateBy(user.getId());
        cu.setCreateTime(DateUtils.formatDatetime(new Date()));
        cu.setDescription(clue.getDescription());
        cu.setId(UUIDUtils.getUUID());
        cu.setName(clue.getCompany());
        cu.setNextContactTime(cu.getNextContactTime());
        cu.setOwner(user.getId());
        cu.setPhone(clue.getPhone());
        cu.setWebsite(clue.getWebsite());
        customerMapper.insertCustomer(cu);

        //3、把该线索中有关个人的信息提取出来，转移到联系人表中
        //封装参数[联系人实例]
        Contacts co = new Contacts();
        co.setAddress(clue.getAddress());
        co.setAppellation(clue.getAppellation());
        co.setContactSummary(clue.getContactSummary());
        co.setCreateBy(user.getId());
        co.setCreateTime(DateUtils.formatDatetime(new Date()));
        co.setCustomerId(cu.getId());
        co.setDescription(clue.getDescription());
        co.setEmail(clue.getEmail());
        co.setFullname(clue.getFullname());
        co.setId(UUIDUtils.getUUID());
        co.setJob(clue.getJob());
        co.setMphone(clue.getMphone());
        co.setNextContactTime(clue.getNextContactTime());
        co.setOwner(user.getId());
        co.setSource(clue.getSource());
        contactsMapper.insertContacts(co);

        //4、根据clueId查询该线索的所有备注
        List<ClueRemark> crList = clueRemarkMapper.selectClueRemarkByClueId(clueId);

        //如果得到的备注不为空，则将备注转移到客户备注表和联系人备注表
        if (crList != null && crList.size() > 0) {
            CustomerRemark cur;
            List<CustomerRemark> curList = new ArrayList<>();
            ContactsRemark cor;
            List<ContactsRemark> corList = new ArrayList<>();

            for (ClueRemark cr : crList) {
                cur = new CustomerRemark();
                cur.setCreateBy(cr.getCreateBy());
                cur.setCreateTime(cr.getCreateTime());
                cur.setCustomerId(cu.getId());
                cur.setEditBy(cr.getEditBy());
                cur.setEditFlag(cr.getEditFlag());
                cur.setEditTime(cr.getEditTime());
                cur.setId(UUIDUtils.getUUID());
                cur.setNoteContent(cr.getNoteContent());
                curList.add(cur);

                cor = new ContactsRemark();
                cor.setCreateBy(cr.getCreateBy());
                cor.setCreateTime(cr.getCreateTime());
                cor.setContactsId(co.getId());
                cor.setEditBy(cr.getEditBy());
                cor.setEditFlag(cr.getEditFlag());
                cor.setEditTime(cr.getEditTime());
                cor.setId(UUIDUtils.getUUID());
                cor.setNoteContent(cr.getNoteContent());
                corList.add(cor);
            }
            //5、将查询到的备注转移到用户备注表
            customerRemarkMapper.insertCustomerRemarkByList(curList);
            //6、将查询到的备注转移到联系人备注表
            contactsRemarkMapper.insertContactsRemarkByList(corList);
        }

        //7、根据clueId查询线索和市场的关联关系
        List<ClueActivityRelation> carList = clueActivityRelationMapper.selectClueActivityRelationByClueId(clueId);

        //若关联关系不为空，则将该关联关系转移到联系人和市场活动的关联关系表中
        if (carList != null && carList.size() > 0) {
            ContactsActivityRelation coar;
            List<ContactsActivityRelation> coarList = new ArrayList<>();

            for (ClueActivityRelation car : carList) {
                coar = new ContactsActivityRelation();
                coar.setId(UUIDUtils.getUUID());
                coar.setActivityId(car.getActivityId());
                coar.setContactsId(co.getId());
                coarList.add(coar);
            }
            //8、将线索和市场活动的关联关系转移到联系人和市场活动的关联关系表中
            contactsActivityRelationMapper.insertContactsActivityRelationByList(coarList);
        }

        //如果需要创建交易
        if ("true".equals(isCreateTran)) {
            //9、向交易表中添加一条记录
            Tran tran = new Tran();
            tran.setActivityId((String) map.get("activityId"));
            tran.setContactsId(co.getId());
            tran.setCreateBy(user.getId());
            tran.setCreateTime(DateUtils.formatDatetime(new Date()));
            tran.setCustomerId(cu.getId());
            tran.setExpectedDate((String) map.get("expectedDate"));
            tran.setId(UUIDUtils.getUUID());
            tran.setMoney((String) map.get("money"));
            tran.setName((String) map.get("name"));
            tran.setOwner(user.getId());
            tran.setStage((String) map.get("stage"));
            tranMapper.insertTran(tran);

            //10、如果线索备注不为空，则将线索备注转移到交易备注表中
            if (crList != null && crList.size() > 0) {
                TranRemark tr;
                List<TranRemark> trList = new ArrayList<>();

                for (ClueRemark cr : crList) {
                    tr = new TranRemark();
                    tr.setCreateBy(cr.getCreateBy());
                    tr.setCreateTime(cr.getCreateTime());
                    tr.setEditBy(cr.getEditBy());
                    tr.setEditFlag(cr.getEditFlag());
                    tr.setEditTime(cr.getEditTime());
                    tr.setId(UUIDUtils.getUUID());
                    tr.setNoteContent(cr.getNoteContent());
                    tr.setTranId(tran.getId());
                    trList.add(tr);
                }
                tranRemarkMapper.insetTranRemarkByList(trList);
            }
        }

        //11、删除线索下的所有备注
        clueRemarkMapper.deleteClueRemarkByClueId(clueId);

        //12、删除该线索与市场活动的关联关系
        clueActivityRelationMapper.deleteClueActivityRelationByClueId(clueId);

        //13、删除该线索
        clueMapper.deleteClueById(clueId);

    }
}
