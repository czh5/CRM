package com.heng.crm.commons.utils;

import com.heng.crm.workbench.domain.Activity;
import org.apache.poi.hssf.usermodel.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//与HSSF相关的工具类
public class HSSFUtils {
    private HSSFUtils() {
    }

    /**
     * 将市场活动导出为Excel文件
     *
     * @param response
     * @param activityList
     */
    public static void ExportActivityForExcel(HttpServletResponse response, List<Activity> activityList) throws IOException {
        //调用Apache-poi插件提供的类和方法创建excel文件
        HSSFWorkbook wb = new HSSFWorkbook();                   //整个文件
        HSSFSheet sheet = wb.createSheet("市场活动");//一页
        //创建表头
        HSSFRow row = sheet.createRow(0);   //第1行
        HSSFCell cell = row.createCell(0);  //第1列
        cell.setCellValue("ID");
        cell = row.createCell(1);  //第2列
        cell.setCellValue("所有者");
        cell = row.createCell(2);  //第3列
        cell.setCellValue("活动名称");
        cell = row.createCell(3);  //第4列
        cell.setCellValue("开始时间");
        cell = row.createCell(4);  //第5列
        cell.setCellValue("结束时间");
        cell = row.createCell(5);  //第6列
        cell.setCellValue("成本");
        cell = row.createCell(6);  //第7列
        cell.setCellValue("描述");
        cell = row.createCell(7);  //第8列
        cell.setCellValue("创建时间");
        cell = row.createCell(8);  //第9列
        cell.setCellValue("创建者");
        cell = row.createCell(9);  //第10列
        cell.setCellValue("修改时间");
        cell = row.createCell(10);  //第11列
        cell.setCellValue("修改者");

        //添加具体内容
        if (activityList != null && activityList.size() > 0) {
            Activity activity = null;
            for (int i = 0; i < activityList.size(); i++) {
                activity = activityList.get(i);
                row = sheet.createRow(i + 1);

                cell = row.createCell(0);  //第1列
                cell.setCellValue(activity.getId());
                cell = row.createCell(1);  //第2列
                cell.setCellValue(activity.getOwner());
                cell = row.createCell(2);  //第3列
                cell.setCellValue(activity.getName());
                cell = row.createCell(3);  //第4列
                cell.setCellValue(activity.getStartDate());
                cell = row.createCell(4);  //第5列
                cell.setCellValue(activity.getEndDate());
                cell = row.createCell(5);  //第6列
                cell.setCellValue(activity.getCost());
                cell = row.createCell(6);  //第7列
                cell.setCellValue(activity.getDescription());
                cell = row.createCell(7);  //第8列
                cell.setCellValue(activity.getCreateTime());
                cell = row.createCell(8);  //第9列
                cell.setCellValue(activity.getCreateBy());
                cell = row.createCell(9);  //第10列
                cell.setCellValue(activity.getEndDate());
                cell = row.createCell(10);  //第11列
                cell.setCellValue(activity.getEditBy());
            }
        }

        //至此，全部数据都已写入wb这个文件中了
        //原本的操作应该是将wb写入磁盘，在下载时再从磁盘中读取，发往浏览器，但是这样效率太低
        //直接将wb通过输出流发往浏览器
        response.setContentType("application/octet-stream;charset=UTF-8");
        //设置响应头，使每个文件都下载，而不是打开
        response.addHeader("Content-Disposition", "attachment;filename=activityList.xls");
        OutputStream out = response.getOutputStream();
        wb.write(out);
        //关闭资源
        wb.close();
        out.flush();
    }

    /**
     * 根据类型获取某一列的内容，获取到的内容以字符串的形式返回
     *
     * @param cell
     * @return
     */
    public static String getCellValueFromExcel(HSSFCell cell) {
        String cellValue = null;
        if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) { //数字
            if (HSSFDateUtil.isCellDateFormatted(cell)) {   //判断是否为日期类型
                //用于转化为日期格式
                Date date = cell.getDateCellValue();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                cellValue = sdf.format(date);
            } else {
                // 用于格式化数字，只保留数字的整数部分
                DecimalFormat df = new DecimalFormat("########");
                cellValue = df.format(cell.getNumericCellValue());
            }
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {   //字符串
            cellValue = cell.getStringCellValue();
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {  //公式
            cellValue = cell.getCellFormula();
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {  //布尔
            cellValue = cell.getBooleanCellValue() + "";
        } else {
            cellValue = "";
        }
        return cellValue;
    }
}
