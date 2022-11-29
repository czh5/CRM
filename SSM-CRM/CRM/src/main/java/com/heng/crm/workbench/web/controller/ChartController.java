package com.heng.crm.workbench.web.controller;

import com.heng.crm.workbench.service.TranService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class ChartController {

    @Resource
    private TranService tranService;

    /**
     * 去往交易图标页面
     * @return
     */
    @RequestMapping("/workbench/chart/transaction/tranChartIndex.do")
    public String tranChartIndex() {
        return "workbench/chart/transaction/index";
    }

    /**
     * 根据阶段分组查询交易数量
     * @return
     */
    @RequestMapping("/workbench/chart/transaction/queryCountOfTranGroupByStage.do")
    @ResponseBody
    public Object queryCountOfTranGroupByStage() {
        return tranService.queryCountOfTranGroupByStage();
    }
}
