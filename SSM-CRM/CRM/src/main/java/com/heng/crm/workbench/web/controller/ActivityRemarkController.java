package com.heng.crm.workbench.web.controller;

import com.heng.crm.commons.constants.Constant;
import com.heng.crm.commons.domain.ReturnObject;
import com.heng.crm.commons.utils.DateUtils;
import com.heng.crm.commons.utils.UUIDUtils;
import com.heng.crm.settings.domain.User;
import com.heng.crm.workbench.domain.ActivityRemark;
import com.heng.crm.workbench.service.ActivityRemarkService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
public class ActivityRemarkController {

    @Resource
    private ActivityRemarkService activityRemarkService;

    /**
     * 添加新的市场备注
     * @param remark    备注对象，保存了市场活动id和备注内容
     * @param session   session
     * @return
     */
    @RequestMapping("/workbench/activity/saveCreateActivityRemark.do")
    @ResponseBody
    public Object saveCreateActivityRemark(ActivityRemark remark, HttpSession session) {
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        //封装参数
        remark.setId(UUIDUtils.getUUID());
        remark.setCreateTime(DateUtils.formatDatetime(new Date()));
        remark.setCreateBy(user.getId());
        remark.setEditFlag(Constant.ACTIVITY_REMARK_FLAG_NO_EDITED);

        ReturnObject ro = new ReturnObject();
        try {
            //调用service层方法添加备注
            int ret = activityRemarkService.saveCreateActivityRemark(remark);

            if (ret > 0) {
                ro.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                remark.setCreateBy(user.getName()); //修改createBy属性由id变为姓名，方便前端使用
                ro.setRetInf(remark);
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
     * 根据id删除市场活动备注
     * @param id id
     * @return
     */
    @RequestMapping("/workbench/activity/deleteActivityRemarkById.do")
    @ResponseBody
    public Object deleteActivityRemarkById(String id) {
        ReturnObject ro = new ReturnObject();
        try {
            //调用service层方法删除备注
            int ret = activityRemarkService.deleteActivityRemarkById(id);

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
     * 修改市场活动备注
     * @param remark remark
     * @return
     */
    @RequestMapping("/workbench/activity/saveEditActivityRemark.do")
    @ResponseBody
    public Object saveEditActivityRemark(ActivityRemark remark, HttpSession session) {
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        //封装参数
        remark.setEditTime(DateUtils.formatDatetime(new Date()));
        remark.setEditBy(user.getId());
        remark.setEditFlag(Constant.ACTIVITY_REMARK_FLAG_YES_EDITED);

        ReturnObject ro = new ReturnObject();
        try {
            //调用service层方法修改市场活动备注
            int ret = activityRemarkService.saveEditActivityRemark(remark);

            if (ret > 0) {
                ro.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                remark.setEditBy(user.getName());
                ro.setRetInf(remark);
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
}
