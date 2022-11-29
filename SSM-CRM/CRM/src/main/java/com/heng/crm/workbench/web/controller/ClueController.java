package com.heng.crm.workbench.web.controller;

import com.heng.crm.commons.constants.Constant;
import com.heng.crm.commons.domain.ReturnObject;
import com.heng.crm.commons.utils.DateUtils;
import com.heng.crm.commons.utils.UUIDUtils;
import com.heng.crm.settings.domain.DicValue;
import com.heng.crm.settings.domain.User;
import com.heng.crm.settings.service.DicValueService;
import com.heng.crm.settings.service.UserService;
import com.heng.crm.workbench.domain.Activity;
import com.heng.crm.workbench.domain.Clue;
import com.heng.crm.workbench.domain.ClueActivityRelation;
import com.heng.crm.workbench.domain.ClueRemark;
import com.heng.crm.workbench.service.ActivityService;
import com.heng.crm.workbench.service.ClueActivityRelationService;
import com.heng.crm.workbench.service.ClueRemarkService;
import com.heng.crm.workbench.service.ClueService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class ClueController {

    @Resource
    private UserService userService;

    @Resource
    private DicValueService dicValueService;

    @Resource
    private ClueService clueService;

    @Resource
    private ClueRemarkService clueRemarkService;

    @Resource
    private ActivityService activityService;

    @Resource
    private ClueActivityRelationService clueActivityRelationService;

    /**
     * 跳转至线索主页面
     * @return
     */
    @RequestMapping("/workbench/clue/index.do")
    public String index() {
        return ("workbench/clue/index");
    }

    /**
     * 加载下拉列表中的选项
     * @return
     */
    @RequestMapping("/workbench/clue/loadSelectOption.do")
    @ResponseBody
    public Map<String, Object> loadSelectOption() {
        Map<String, Object> map = new HashMap<>();
        //调用service层方法查询下拉列表的选项
        List<User> userList = userService.queryAllUser();   //所有者列表
        List<DicValue> appellationList = dicValueService.queryDicValueByTypeCode("appellation");    //称呼列表
        List<DicValue> clueStateList = dicValueService.queryDicValueByTypeCode("clueState");        //线索状态
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");              //线索来源
        //封装数据
        map.put("userList",userList);
        map.put("appellationList",appellationList);
        map.put("clueStateList",clueStateList);
        map.put("sourceList",sourceList);

        return map;
    }

    /**
     * 根据条件分页查询线索
     * @param fullName  名称
     * @param company   公司
     * @param phone     座机
     * @param source    来源
     * @param owner     所有者
     * @param mphone    手机
     * @param state     状态
     * @param pageNo    页号
     * @param pageSize  每页条数
     * @return
     */
    @RequestMapping("/workbench/clue/queryClueByConditionForPage.do")
    @ResponseBody
    public Object queryClueByConditionForPage(String fullName, String company, String phone, String source,
                                              String owner, String mphone, String state, int pageNo, int pageSize) {
        //封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("fullName",fullName);
        map.put("company",company);
        map.put("phone",phone);
        map.put("source",source);
        map.put("owner",owner);
        map.put("mphone",mphone);
        map.put("state",state);
        map.put("beginNo",(pageNo-1)*pageSize);
        map.put("pageSize",pageSize);
        //调用service层方法查询
        List<Clue> clueList = clueService.queryClueByConditionForPage(map);
        int totalRows = clueService.queryCountOfCLueByCondition(map);
        //返回响应结果
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("clueList",clueList);
        retMap.put("totalRows",totalRows);

        return retMap;
    }

    /**
     * 插入新的线索
     * @param clue  线索
     * @param session   session
     * @return
     */
    @RequestMapping("/workbench/clue/saveCreateClue.do")
    @ResponseBody
    public Object saveCreateClue(Clue clue, HttpSession session) {
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        //封装参数
        clue.setId(UUIDUtils.getUUID());
        clue.setCreateBy(user.getId());
        clue.setCreateTime(DateUtils.formatDatetime(new Date()));

        ReturnObject ro = new ReturnObject();
        try {
            //调用service层方法插入新数据
            int ret = clueService.saveCreateClue(clue);

            if (ret > 0) {
                ro.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                ro.setCode(Constant.RETURN_OBJECT_CODE_FAILURE);
                ro.setMessage("系统繁忙,请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ro.setCode(Constant.RETURN_OBJECT_CODE_FAILURE);
            ro.setMessage("系统繁忙,请稍后重试...");
        }

        return ro;
    }

    /**
     * 去往明细页面
     * @return
     */
    @RequestMapping("/workbench/clue/toDetail.do")
    public String toDetail() {
        return "workbench/clue/detail";
    }

    /**
     * 根据id加载线索明细、备注和市场活动
     * @param id    id
     * @return
     */
    @RequestMapping("/workbench/clue/loadClueDetailAndRemark.do")
    @ResponseBody
    public Object loadClueDetailAndRemark(String id) {
        Map<String, Object> map = new HashMap<>();
        //调用service层方法查询
        Clue clue = clueService.queryClueForDetailById(id);
        List<ClueRemark> clueRemarkList = clueRemarkService.queryClueRemarkByClueId(id);
        List<Activity> activityList = activityService.queryActivityForDetailByClueId(id);

        //封装
        map.put("clue",clue);
        map.put("clueRemarkList",clueRemarkList);
        map.put("activityList",activityList);

        return map;
    }

    /**
     * 根据市场活动名和线索id查询市场活动明细
     * @param activityName
     * @param clueId
     * @return
     */
    @RequestMapping("/workbench/clue/queryActivityForDetailByActivityNameAndClueId.do")
    @ResponseBody
    public Object queryActivityForDetailByActivityNameAndClueId(String activityName, String clueId) {
        //封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("activityName",activityName);
        map.put("clueId",clueId);

        //调用service层方法查询当前线索未关联的市场活动
        List<Activity> activityList = activityService.queryActivityForDetailByActivityNameAndClueId(map);

        return activityList;
    }

    /**
     * 保存新关联的市场活动记录
     * @param id
     * @param clueId
     * @return
     */
    @RequestMapping("/workbench/clue/saveClueActivityRelationByList.do")
    @ResponseBody
    public Object saveClueActivityRelationByList(String[] id, String clueId) {
        List<ClueActivityRelation> list = new ArrayList<>();
        ClueActivityRelation car;

        //将id数组和clueId封装成实体类对象
        for (int i = 0; i < id.length; i++) {
            car = new ClueActivityRelation();
            car.setId(UUIDUtils.getUUID());
            car.setActivityId(id[i]);
            car.setClueId(clueId);
            list.add(car);
        }

        ReturnObject ro = new ReturnObject();
        try {
            //调用service层方法插入新的关联数据
            int ret = clueActivityRelationService.saveClueActivityRelationByList(list);

            if (ret > 0) {
                //保存成功，则查询最新关联的市场活动的明细，用于前端展示
                ro.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                ro.setRetInf(activityService.queryActivityForDetailByIds(id));
            } else {
                ro.setCode(Constant.RETURN_OBJECT_CODE_FAILURE);
                ro.setMessage("系统繁忙,请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ro.setCode(Constant.RETURN_OBJECT_CODE_FAILURE);
            ro.setMessage("系统繁忙,请稍后重试...");
        }

        return ro;
    }

    /**
     * 根据clueId和activityId删除线索和市场活动的关联关系
     * @param relation
     * @return
     */
    @RequestMapping("/workbench/clue/deleteClueActivityRelationByClueIdAndActivityId.do")
    @ResponseBody
    public Object deleteClueActivityRelationByClueIdAndActivityId(ClueActivityRelation relation) {
        ReturnObject ro = new ReturnObject();
        try {
            //调用service层方法删除关联关系
            int ret = clueActivityRelationService.deleteClueActivityRelationByClueIdAndActivityId(relation);

            if (ret > 0) {
                //删除成功
                ro.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                ro.setCode(Constant.RETURN_OBJECT_CODE_FAILURE);
                ro.setMessage("系统繁忙,请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ro.setCode(Constant.RETURN_OBJECT_CODE_FAILURE);
            ro.setMessage("系统繁忙,请稍后重试...");
        }

        return ro;
    }

    /**
     * 跳转至线索转换页面
     * @return
     */
    @RequestMapping("/workbench/clue/toConvert.do")
    public String toConvert() {
        return "workbench/clue/convert";
    }

    /**
     * 加载线索转换页面
     * @param clueId
     * @return
     */
    @RequestMapping("/workbench/clue/loadConvert.do")
    @ResponseBody
    public Object loadConvert(String clueId) {
        //调用service层方法
        Clue clueDetail = clueService.queryClueForDetailById(clueId);
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");    //流程

        //封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("clueDetail", clueDetail);
        map.put("stageList", stageList);

        return map;
    }

    /**
     * 根据activityName和clueId查询已关联的市场活动
     * @param activityName
     * @param clueId
     * @return
     */
    @RequestMapping("/workbench/clue/queryActivityForConvertByActivityNameAndClueId.do")
    @ResponseBody
    public Object queryActivityForConvertByActivityNameAndClueId(String activityName, String clueId) {
        //封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("activityName", activityName);
        map.put("clueId", clueId);
        //调用service层方法查询
        List<Activity> activityList = activityService.queryActivityForConvertByActivityNameAndClueId(map);

        return activityList;
    }

    /**
     * 保存线索转换
     * @param clueId    线索id
     * @param money     金额
     * @param name      交易名称
     * @param expectedDate  预计成交日期
     * @param stage     流程
     * @param activityId    市场活动源
     * @param isCreateTran  是否创建交易
     * @param session session
     * @return
     */
    @RequestMapping("/workbench/clue/saveConvertClue.do")
    @ResponseBody
    public Object saveConvertClue(String clueId, String money, String name, String expectedDate, String stage,
                                  String activityId, String isCreateTran, HttpSession session) {
        //封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("clueId", clueId);
        map.put("money", money);
        map.put("name", name);
        map.put("expectedDate", expectedDate);
        map.put("stage", stage);
        map.put("activityId", activityId);
        map.put("isCreateTran", isCreateTran);
        map.put(Constant.SESSION_USER,session.getAttribute(Constant.SESSION_USER));

        ReturnObject ro = new ReturnObject();
        try {
            //调用service层方法保存线索转换
            clueService.saveConvertClue(map);

            //如果能正常执行就认为是转换成功了
            ro.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            ro.setCode(Constant.RETURN_OBJECT_CODE_FAILURE);
            ro.setMessage("系统繁忙,请稍后重试...");
        }

        return ro;
    }


}
