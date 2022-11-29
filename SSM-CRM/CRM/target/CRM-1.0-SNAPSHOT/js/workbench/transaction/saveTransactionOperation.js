$(function () {
    //初始时发送请求加载下拉列表中的内容
    $.ajax({
        url : "workbench/transaction/saveInit.do",
        type : "post",
        dataType : "json",
        success : function (data) {
            //填充“阶段”下拉列表的内容
            $("#create-owner").html("<option></option>");
            $.each(data.userList,function () {
                $("#create-owner").append("<option value='"+this.id+"'>" + this.name + "</option>");
            });

            //填充“阶段”下拉列表的内容
            $("#create-stage").html("<option></option>");
            $.each(data.stageList,function () {
                $("#create-stage").append("<option value='"+this.id+"'>" + this.value + "</option>");
            });

            //填充“类型”下拉列表的内容
            $("#create-type").html("<option></option>");
            $.each(data.transactionTypeList,function () {
                $("#create-type").append("<option value='"+this.id+"'>" + this.value + "</option>");
            });

            //填充“类型”下拉列表的内容
            $("#create-source").html("<option></option>");
            $.each(data.sourceList,function () {
                $("#create-source").append("<option value='"+this.id+"'>" + this.value + "</option>");
            });
        }
    });

    //为“市场活动源”的查询按钮绑定单击事件
    $("#queryActivitySource").click(function () {
        //弹出模态窗口
        $("#findMarketActivity").modal("show");
    });

    //为“联系人名称”的查询按钮绑定单击事件
    $("#queryContacts").click(function () {
        //弹出模态窗口
        $("#findContacts").modal("show");
    });

    //先写死数据源和联系人，后期完善
    $("#create-activityId").val("122c90e0979e4520a9edd348b83b4404");
    $("#create-activitySrc").val("活动05");
    $("#create-contactsId").val("dd0ca6fa4bbb425a87b42ba7c901edce");
    $("#create-contactsName").val("小李");

    //为阶段的下拉列表绑定改变事件，获取可能性
    $("#create-stage").change(function () {
        //获取选中的内容
        //var stageValue = $("#create-stage option:selected").text();
        var stageValue = $(this).find("option:selected").text();
        //表单验证
        if (stageValue == "") {
            //清空可能性
            $("#create-possibility").val("");
            return;
        }
        //发送请求
        $.ajax({
            url : "workbench/transaction/getPossibilityByStageValue.do",
            data : {
                stageValue : stageValue
            },
            type : "post",
            dataType : "json",
            success : function (data) {
                //修改可能性
                $("#create-possibility").val(data + "%");
            }
        })
    });

    //客户名称支持自动补全
    //使用插件typeahead，客户名称的输入框调用工具函数
    //当键盘弹起时自动执行
    $("#create-accountName").typeahead({
        //当容器发生键盘弹起事件时，会从source中查找是否有匹配的进行展示，source的值是一个字符数组
        //source可以是静态的，即写死的，如：['xx','xx']
        //也可以是动态的，以function(jquery,process){}作为参数
        //其中jquery保存的是当前容器的内容，process是一个函数，process(字符数组)的执行结果是将字符数据赋值给source
        source : function (jquery, process) {
            $.ajax({
                url : "workbench/transaction/queryCustomerNameByName.do",
                data : {
                    customerName : jquery
                },
                type : "post",
                dataType : "json",
                success : function (data) {
                    process(data);
                }
            })
        }
    });

    //为创建交易的保存按钮绑定单击事件
    $("#saveCreateTranBtn").click(function () {
        //收集参数
        var owner = $("#create-owner").val();
        var money = $.trim($("#create-amountOfMoney").val());
        var name = $.trim($("#create-transactionName").val());
        var expectedDate = $("#create-expectedClosingDate").val();
        var customerName = $.trim($("#create-accountName").val());
        var stage = $("#create-stage").val();
        var type = $("#create-type").val();
        var source = $("#create-source").val();
        var activityId = $("#create-activityId").val();
        var contactsId = $("#create-contactsId").val();
        var description = $.trim($("#create-description").val());
        var contactSummary = $.trim($("#create-contactSummary").val());
        var nextContactTime = $("#create-nextContactTime").val();

        //表单验证
        if (owner == "") {
            alert("所有者不能为空");
            return;
        }
        if (money == "") {
            alert("金额不能为空");
            return;
        }
        var moneyReg = /^(([1-9]\d*)|0)$/;
        if (!moneyReg.test(money)) {
            alert("金额应为非负整数");
            return;
        }
        if (name == "") {
            alert("名称不能为空");
            return;
        }
        if (expectedDate == "") {
            alert("预计成交日期不能为空");
            return;
        }
        if (customerName == "") {
            alert("客户名称不能为空");
            return;
        }
        if (stage == "") {
            alert("阶段不能为空");
            return;
        }


        //发送请求
        $.ajax({
            url : "workbench/transaction/saveCreateTran.do",
            data : {
                owner : owner,
                money : money,
                name : name,
                expectedDate : expectedDate,
                customerName : customerName,
                stage : stage,
                type : type,
                source : source,
                activityId : activityId,
                contactsId : contactsId,
                description : description,
                contactSummary : contactSummary,
                nextContactTime : nextContactTime
            },
            type : "post",
            dataType : "json",
            success : function (data) {
                if (data.code == "1") {
                    //成功则返回显示页面
                    window.location.href = "workbench/transaction/toTran.do";
                } else {
                    //弹出提示信息，页面不跳转
                    alert(data.message);
                }
            }
        })
    });


});