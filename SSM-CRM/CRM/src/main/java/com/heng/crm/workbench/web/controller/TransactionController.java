package com.heng.crm.workbench.web.controller;

import com.heng.crm.commons.constants.Constant;
import com.heng.crm.commons.domain.ReturnObject;
import com.heng.crm.settings.domain.DicValue;
import com.heng.crm.settings.domain.User;
import com.heng.crm.settings.service.DicValueService;
import com.heng.crm.settings.service.UserService;
import com.heng.crm.workbench.domain.Tran;
import com.heng.crm.workbench.domain.TranHistory;
import com.heng.crm.workbench.domain.TranRemark;
import com.heng.crm.workbench.service.CustomerService;
import com.heng.crm.workbench.service.TranHistoryService;
import com.heng.crm.workbench.service.TranRemarkService;
import com.heng.crm.workbench.service.TranService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Controller
public class TransactionController {

    @Resource
    private DicValueService dicValueService;

    @Resource
    private TranService tranService;

    @Resource
    private UserService userService;

    @Resource
    private CustomerService customerService;

    @Resource
    private TranRemarkService tranRemarkService;

    @Resource
    private TranHistoryService tranHistoryService;

    /**
     * 去往交易页面
     * @return
     */
    @RequestMapping("/workbench/transaction/toTran.do")
    public String toTran() {
        return "workbench/transaction/index";
    }

    /**
     * 加载下拉列表数据
     * @return
     */
    @RequestMapping("/workbench/transaction/loadSelectOptions.do")
    @ResponseBody
    public Object loadSelectOptions() {
        Map<String, Object> map = new HashMap<>();
        //调用service层方法查询阶段、类型、来源下拉列表的内容
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        List<DicValue> transactionTypeList = dicValueService.queryDicValueByTypeCode("transactionType");
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        //封装
        map.put("stageList",stageList);
        map.put("transactionTypeList",transactionTypeList);
        map.put("sourceList",sourceList);

        return map;
    }

    /**
     * 根据条件分页查询交易记录
     * @param name          名称
     * @param customerId    客户名称
     * @param stage         阶段
     * @param type          类型
     * @param owner         所有者
     * @param source        来源
     * @param contactsId    联系人名称
     * @return
     */
    @RequestMapping("/workbench/transaction/queryTranByConditionForPage.do")
    @ResponseBody
    public Object queryTranByConditionForPage(String name, String customerId, String stage, String type, String owner,
                                                String source, String contactsId, int pageNo, int pageSize) {
        //封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("customerId",customerId);
        map.put("stage",stage);
        map.put("type",type);
        map.put("owner",owner);
        map.put("source",source);
        map.put("contactsId",contactsId);
        map.put("beginNo",(pageNo-1)*pageSize);
        map.put("pageSize",pageSize);
        //调用service层方法查询
        List<Tran> tranList = tranService.queryTranByConditionForPage(map);
        int totalRows = tranService.queryCountOfTranByCondition(map);

        //返回
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("tranList",tranList);
        retMap.put("totalRows",totalRows);

        return retMap;
    }

    /**
     * 去往创建交易页面
     * @return
     */
    @RequestMapping("/workbench/transaction/toSave.do")
    public String toSave() {
        return "workbench/transaction/save";
    }

    /**
     * 加载创建交易页面的初始内容
     * @return
     */
    @RequestMapping("/workbench/transaction/saveInit.do")
    @ResponseBody
    public Object saveInit() {
        Map<String, Object> map = new HashMap<>();
        //调用service层方法查询所有者、阶段、类型、来源下拉列表的内容
        List<User> userList = userService.queryAllUser();
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        List<DicValue> transactionTypeList = dicValueService.queryDicValueByTypeCode("transactionType");
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        //封装
        map.put("userList",userList);
        map.put("stageList",stageList);
        map.put("transactionTypeList",transactionTypeList);
        map.put("sourceList",sourceList);

        return map;
    }

    /**
     * 根据阶段名获取对应的可能性
     * @param stageValue    阶段名
     * @return
     */
    @RequestMapping("/workbench/transaction/getPossibilityByStageValue.do")
    @ResponseBody
    public Object getPossibilityByStageValue(String stageValue) {
        //解析配置文件，获取可能性
        return ResourceBundle.getBundle("transaction/possibility").getString(stageValue);
    }

    /**
     * 根据客户名模糊查询客户名
     * @param customerName
     * @return
     */
    @RequestMapping("/workbench/transaction/queryCustomerNameByName.do")
    @ResponseBody
    public Object queryCustomerNameByName(String customerName) {
        //调用service层方法模糊查询可能的客户名
        return customerService.queryCustomerNameByName(customerName);
    }

    /**
     * 保存创建的交易记录
     * @param map       封装了tran的大多数属性，但由于传过来的是客户名，需要保存的是客户id，因此不用实体类
     * @param session   session
     * @return
     */
    @RequestMapping("/workbench/transaction/saveCreateTran.do")
    @ResponseBody
    public Object saveCreateTran(@RequestParam Map<String, Object> map, HttpSession session) {
        //放入当前用户信息
        map.put(Constant.SESSION_USER, session.getAttribute(Constant.SESSION_USER));

        ReturnObject ro = new ReturnObject();
        try {
            tranService.saveCreateTran(map);
            //如果没有报错就当做是顺利执行
            ro.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            ro.setCode(Constant.RETURN_OBJECT_CODE_FAILURE);
            ro.setMessage("系统繁忙,请稍后重试...");
        }
        return ro;
    }

    /**
     * 去往交易明细页面
     * @return
     */
    @RequestMapping("/workbench/transaction/toDetail.do")
    public String toDetail() {
        return "workbench/transaction/detail";
    }

    @RequestMapping("/workbench/transaction/loadDetail.do")
    @ResponseBody
    public Object loadDetail(String id) {
        //调用service层方法查询交易明细
        Tran tran = tranService.queryTranForDetailById(id);
        //根据交易的阶段查询可能性
        String possibility = ResourceBundle.getBundle("transaction/possibility").getString(tran.getStage());
        //调用service层方法查询备注
        List<TranRemark> tranRemarkList = tranRemarkService.queryTranRemarkForDetailByTranId(id);
        //调用service层方法查询交易历史
        List<TranHistory> tranHistoryList = tranHistoryService.queryTranHistoryForDetailByTranId(id);
        //调用service层方法查询所有阶段
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");

        Map<String, Object> retMap = new HashMap<>();
        retMap.put("tran",tran);
        retMap.put("possibility",possibility);
        retMap.put("tranRemarkList",tranRemarkList);
        retMap.put("tranHistoryList",tranHistoryList);
        retMap.put("stageList",stageList);

        return  retMap;
    }
}
