$(function () {
    //初始时发送请求加载下拉列表中的内容
    $.ajax({
        url : "workbench/transaction/loadSelectOptions.do",
        type : "post",
        dataType : "json",
        success : function (data) {
            //填充“阶段”下拉列表的内容
            $("#query-stage").html("<option></option>");
            $.each(data.stageList,function () {
                $("#query-stage").append("<option value='"+this.id+"'>" + this.value + "</option>");
            });

            //填充“类型”下拉列表的内容
            $("#query-transactionType").html("<option></option>");
            $.each(data.transactionTypeList,function () {
                $("#query-transactionType").append("<option value='"+this.id+"'>" + this.value + "</option>");
            });

            //填充“类型”下拉列表的内容
            $("#query-source").html("<option></option>");
            $.each(data.sourceList,function () {
                $("#query-source").append("<option value='"+this.id+"'>" + this.value + "</option>");
            });

        }
    });

    //初始时查询第一页的10条数据
    queryTranByConditionForPage(1,10);

    //点击查询按钮时查询第一页，条数保持不变
    $("#queryTranBtn").click(function () {
        queryTranByConditionForPage(1,$("#page-control").bs_pagination("getOption","rowsPerPage"));
    })

});

//根据条件分页查询交易记录
queryTranByConditionForPage = function (pageNo, pageSize) {
    //收集参数
    var owner = $("#query-owner").val();
    var name = $("#query-name").val();
    var customerId = $("#query-customerId").val();
    var stage = $("#query-stage").val();
    var type = $("#query-transactionType").val();
    var source = $("#query-source").val();
    var contactsId = $("#query-contactsId").val();
    //发送请求
    $.ajax({
        url : "workbench/transaction/queryTranByConditionForPage.do",
        data : {
            owner : owner,
            name : name,
            customerId : customerId,
            stage : stage,
            type : type,
            source : source,
            contactsId : contactsId,
            pageNo : pageNo,
            pageSize : pageSize
        },
        type : "post",
        dataType : "json",
        success : function (data) {
            //展示交易记录
            var trans = "";
            $.each(data.tranList,function () {
                trans += "<tr class=\"active\">";
                trans += "<td><input type=\"checkbox\" value='"+this.id+"'/></td>";
                trans += "<td><a style=\"text-decoration: none; cursor: pointer;\"\n" +
                            "onclick=\"window.location.href='workbench/transaction/toDetail.do?id="+this.id+"';\">" + this.name + "</a></td>";
                trans += "<td>" + this.customerId + "</td>";
                trans += "<td>" + this.stage + "</td>";
                trans += "<td>" + this.type + "</td>";
                trans += "<td>" + this.owner + "</td>";
                trans += "<td>" + this.source + "</td>";
                trans += "<td>" + this.contactsId + "</td>";
                trans += "</tr>";
            });
            $("#query-tBody").html(trans);

            //对容器调用bs_pagination工具函数，显示翻页信息
            $("#page-control").bs_pagination({
                currentPage : pageNo,       //当前页，相当于pageNo
                rowsPerPage : pageSize,     //每页行数，相当于pageSize
                totalRows : data.totalRows, //总记录条数
                totalPages : Math.ceil(data.totalRows/pageSize), //总页数，必填项
                visiblePageLinks : 5,       //最多显示的卡片数

                //用户每次切换页号都会自动调用该函数
                //该函数返回切换页号之后的pageNo和pageSize
                onChangePage : function (event,pageObj) {
                    //event表示翻页这个事件，pageObj表示这个翻页对象
                    queryClueByConditionForPage(pageObj.currentPage,pageObj.rowsPerPage);
                }
            });

        }
    })
};