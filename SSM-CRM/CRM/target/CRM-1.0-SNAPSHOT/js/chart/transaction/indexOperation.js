$(function () {
    //当容器加载完毕后，查询阶段的名称和数量，调用eCharts的工具函数，显示漏斗图
    $.ajax({
        url : "workbench/chart/transaction/queryCountOfTranGroupByStage.do",
        type : "post",
        dataType : "json",
        success : function (data) {
            //调用eCharts的工具函数，显示漏斗图
            //基于准备好的容器，初始化eCharts实例
            var tranChart = echarts.init(document.getElementById('tranStageChart'));

            //指定图表的配置项和数据
            var option = {
                title: {
                    text: '交易统计图表',
                    subtext: '交易中各阶段的数据量'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: '{a} <br/>{b} : {c}'
                },
                toolbox: {
                    feature: {
                        dataView: { readOnly: false },
                        restore: {},
                        saveAsImage: {}
                    }
                },
                series: [
                    {
                        name: '数据量',
                        type: 'funnel',
                        left: '10%',
                        width: '80%',
                        label: {
                            formatter: '{b}'
                        },
                        labelLine: {
                            show: true
                        },
                        itemStyle: {
                            opacity: 0.7
                        },
                        emphasis: {
                            label: {
                                position: 'inside',
                                formatter: '{b}: {c}'
                            }
                        },
                        //放入stage的数量和名称
                        data: data
                    }
                ]
            };

            //使用指定的配置项生成图表
            tranChart.setOption(option);
        }
    });
});