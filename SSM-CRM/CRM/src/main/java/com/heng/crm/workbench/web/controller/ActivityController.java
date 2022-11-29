package com.heng.crm.workbench.web.controller;

import com.heng.crm.commons.constants.Constant;
import com.heng.crm.commons.domain.ReturnObject;
import com.heng.crm.commons.utils.DateUtils;
import com.heng.crm.commons.utils.UUIDUtils;
import com.heng.crm.settings.domain.User;
import com.heng.crm.settings.service.UserService;
import com.heng.crm.workbench.domain.Activity;
import com.heng.crm.workbench.domain.ActivityRemark;
import com.heng.crm.workbench.service.ActivityRemarkService;
import com.heng.crm.workbench.service.ActivityService;
import com.heng.crm.commons.utils.HSSFUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;


@Controller
public class ActivityController {

    @Resource
    private UserService userService;

    @Resource
    private ActivityService activityService;

    @Resource
    private ActivityRemarkService activityRemarkService;

    /**
     * 跳转至用户活动页面
     * @return
     */
    @RequestMapping("/workbench/activity/index.do")
    public String index() {
        return "workbench/activity/index";
    }

    /**
     * 加载用户活动页面的用户名下拉列表
     * @return
     */
    @RequestMapping("/workbench/activity/loadUsername.do")
    @ResponseBody
    public Object loadUsername() {
        return userService.queryAllUser();
    }

    /**
     * 添加新创建的市场活动
     * @param activity  市场活动对象
     * @param session   session
     * @return
     */
    @RequestMapping("/workbench/activity/saveCreateActivity.do")
    @ResponseBody
    public Object saveCreateActivity(Activity activity, HttpSession session) {
        User user = (User)session.getAttribute(Constant.SESSION_USER);
        //二次封装，前端只封装了6个数据，插入时需要9个
        activity.setId(UUIDUtils.getUUID());
        activity.setCreateTime(DateUtils.formatDatetime(new Date()));
        activity.setCreateBy(user.getId());

        ReturnObject ro = new ReturnObject();
        try {
            //调用service层方法插入数据，插入数据要考虑异常问题
            int ret = activityService.saveCreateActivity(activity);

            if (ret > 0) {  //插入成功
                ro.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            } else {    //插入失败
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
     * 根据条件分页查询市场活动
     * @param name      活动名称
     * @param owner     所有人
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param pageNo    第几页
     * @param pageSize  每页条数
     * @return
     */
    @RequestMapping("/workbench/activity/queryActivityByConditionForPage.do")
    @ResponseBody
    public Object queryActivityByConditionForPage(String name, String owner, String startDate, String endDate,
                                                  int pageNo, int pageSize) {
        //封装数据
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("beginNo",(pageNo-1)*pageSize);
        map.put("pageSize", pageSize);
        //调用service层方法查询
        List<Activity> activityList = activityService.queryActivityByConditionForPage(map);
        int totalRows = activityService.queryCountOfActivityByCondition(map);
        //返回响应结果
        Map<String,Object> retMap = new HashMap<>();
        retMap.put("activityList",activityList);
        retMap.put("totalRows",totalRows);

        return retMap;
    }

    /**
     * 根据id数组删除市场活动
     * @param id    id数组
     * @return
     */
    @RequestMapping("/workbench/activity/deleteActivityByIds.do")
    @ResponseBody
    public Object deleteActivityByIds(String[] id) {
        System.out.println(id);
        ReturnObject ro = new ReturnObject();
        try {
            //调用service层方法删除数据
            int ret = activityService.deleteActivityByIds(id);
            if (ret > 0) {  //删除成功
                ro.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            } else {    //删除失败
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
     * 根据id查询市场活动
     * @param id
     * @return
     */
    @RequestMapping("/workbench/activity/selectActivityById.do")
    @ResponseBody
    public Object selectActivityById(String id) {
        //调用service层方法查询市场活动
        return activityService.queryActivityById(id);
    }

    /**
     * 保存修改的市场活动
     * @param activity  活动
     * @param session   session
     * @return
     */
    @RequestMapping("/workbench/activity/saveEditActivity.do")
    @ResponseBody
    public Object saveEditActivity(Activity activity, HttpSession session) {
        //封装参数，前端只提供了7个，仍需2个
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        activity.setEditTime(DateUtils.formatDatetime(new Date()));
        activity.setEditBy(user.getId());

        ReturnObject ro = new ReturnObject();
        try {
            //调用service层方法修改数据
            int ret = activityService.saveEditActivity(activity);

            if (ret == 1) { //修改成功
                ro.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            } else {    //修改失败
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
     *  导出全部市场活动
     * @param response  request
     * @throws IOException
     * 该方法的返回值是void，这是因为往常我们都返回json或数据，SpringMVC不擅长返回文件
     * 因此由我们自己返回，使用response
     */
    @RequestMapping("/workbench/activity/exportAllActivity.do")
    public void exportAllActivity(HttpServletResponse response) throws IOException {
        //调用service层方法查询所有市场活动
        List<Activity> activityList = activityService.queryAllActivity();
        //调用工具类方法将市场活动导出为Excel文件
        HSSFUtils.ExportActivityForExcel(response,activityList);
    }

    /**
     * 根据需要导出部分市场活动
     * @param id    需要的市场活动id
     * @param response  response
     * @throws IOException
     */
    @RequestMapping("/workbench/activity/exportSomeActivityByIds.do")
    public void exportSomeActivityByIds(String[] id, HttpServletResponse response) throws IOException {
        //调用service层方法根据id查询需要的市场活动
        List<Activity> activityList = activityService.querySomeActivityByIds(id);
        //调用工具类方法将市场活动导出为Excel文件
        HSSFUtils.ExportActivityForExcel(response,activityList);
    }

    /**
     * 当用户要上传市场活动时，要求用户按模板填写，给用户提供模板的下载
     * @param response  response
     * @throws IOException
     */
    @RequestMapping("/workbench/activity/downloadMould.do")
    public void downloadMould(HttpServletResponse response) throws IOException {
        //生成Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("市场活动");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("活动名称");
        cell = row.createCell(1);
        cell.setCellValue("开始时间");
        cell = row.createCell(2);
        cell.setCellValue("结束时间");
        cell = row.createCell(3);
        cell.setCellValue("成本");
        cell = row.createCell(4);
        cell.setCellValue("描述");
        //下载Excel文件
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.addHeader("Content-Disposition","attachment;filename=activityListMould.xls");
        OutputStream out = response.getOutputStream();
        wb.write(out);
        //关闭资源
        wb.close();
        out.flush();
    }

    /**
     * 导入市场活动
     * @param activityFile  SpringMVC提供的用于接收文件的类型
     * @param session session
     * @return
     * 注意：用于接收文件的类开始时并不在容器中，因此需要创建该类的实例，调用实例来接收数据
     * 在SpringMVC配置文件中加入文件上传解析器
     *
     * 对于一个市场活动，有以下属性：id、所有者、名称、开始时间、结束时间、成本、描述、创建时间、创建者。其中
     * 1、为保证唯一性，id由后台生成；
     * 2、所有者是记录用户id的，有四种方式：(都需要在用户调研时确定)
     *          用户填写所有者姓名，后台查id。效率低且可能出现同名，即一个名字查出多个id
     *          在文件中添加附录，提示姓名和id的对应关系，由用户填写id，对于中小数据量可采取，容易混乱
     *          使用公共账号
     *          默认导入该文件的用户就是所有者(本方法中采用这种)
     * 3、创建时间默认为导入文件时的时间
     * 4、创建者为导入文件的用户
     * 5、剩余属性由用户自行填写
     */
    @RequestMapping("/workbench/activity/importActivity.do")
    @ResponseBody
    public Object importActivity(MultipartFile activityFile, HttpSession session) {
        ReturnObject ro = new ReturnObject();
        //正常来说应该是获取文件，写入磁盘；再从磁盘中读取，加入数据库，但是这样效率太低
        //可以直接得到文件的输入流，写入workbook中
        try {
            User user = (User) session.getAttribute(Constant.SESSION_USER);
            InputStream is = activityFile.getInputStream();
            HSSFWorkbook wb = new HSSFWorkbook(is);
            HSSFSheet sheet = wb.getSheetAt(0); //获取第一页

            //根据页获取每一行，由于第1行是表头，从第2行开始
            HSSFRow row = null;
            HSSFCell cell = null;
            String cellValue = null;
            Activity activity = null;
            List<Activity> activityList = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {   //getLastRowNum()得到的是最后一行的下标
                row = sheet.getRow(i);
                activity = new Activity();
                for (int j = 0; j < row.getLastCellNum(); j++) {   //getLastCellNum()得到的是最后一列的下标+1，即第几列
                    cell = row.getCell(j);
                    cellValue = HSSFUtils.getCellValueFromExcel(cell);

                    //根据列号设置不同的属性
                    if (j == 0) {   //活动名称
                        activity.setName(cellValue);
                    } else if (j == 1) {    //开始时间
                        System.out.println(cellValue);
                        activity.setStartDate(cellValue);
                    } else if (j == 2) {    //结束时间
                        activity.setEndDate(cellValue);
                    } else if (j == 3) {    //成本
                        activity.setCost(cellValue);
                    } else if (j == 4) {    //描述
                        activity.setDescription(cellValue);
                    }
                }
                //封装其他数据
                activity.setId(UUIDUtils.getUUID());
                activity.setOwner(user.getId());
                activity.setCreateTime(DateUtils.formatDatetime(new Date()));
                activity.setCreateBy(user.getId());
                //放入list集合中
                activityList.add(activity);
            }

            //调用service层方法保存数据
            int ret = activityService.saveCreateActivityByList(activityList);

            //只要能执行到此处就算成功，提示执行结果
            ro.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            ro.setRetInf(ret);
        } catch (IOException e) {
            e.printStackTrace();
            ro.setCode(Constant.RETURN_OBJECT_CODE_FAILURE);
            ro.setMessage("系统繁忙，请稍后重试...");
        }

        return ro;
    }

    /**
     * 跳转至detail页面
     * @return
     */
    @RequestMapping("/workbench/activity/toDetail.do")
    public String toDetail() {
        return "workbench/activity/detail";
    }

    @RequestMapping("/workbench/activity/loadActivityDetailAndRemark.do")
    @ResponseBody
    public Map<String,Object> loadActivityDetailAndRemark(String activityId) {
        Map<String,Object> map = new HashMap<>();
        //调用service层查询市场活动明细和备注
        Activity activity = activityService.queryActivityForDetailById(activityId);
        List<ActivityRemark> arList = activityRemarkService.queryActivityRemarkForDetailByActivityId(activityId);
        //封装参数
        map.put("activity",activity);
        map.put("activityRemarkList",arList);
        return map;
    }
}
