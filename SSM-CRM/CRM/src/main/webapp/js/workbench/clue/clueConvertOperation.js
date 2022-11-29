$(function () {
    //当前线索的id
    var clueId = window.location.search.split("=")[1];

    //加载convert页面的初始内容
    $.ajax({
        url : "workbench/clue/loadConvert.do",
        data : {
            clueId : clueId
        },
        type : "post",
        dataType : "json",
        success : function (data) {
            var clueDetail = data.clueDetail;
            //填充内容
            $("#convert-title").text(clueDetail.fullname + clueDetail.appellation + "-" + clueDetail.company);
            $("#create-customer").text("新建客户：" + clueDetail.company);
            $("#create-contact").text("新建联系人：" + clueDetail.fullname + clueDetail.appellation);
            $("#tradeName").val(clueDetail.company + "-");
            $("#convert-owner").text(clueDetail.owner);
            //遍历流程，放入下拉列表
            var stage = "<option></option>";
            $.each(data.stageList,function () {
                stage += "<option value='"+this.id+"'>" + this.value + "</option>";
            });
            $("#stage").html(stage);
        }
    });

    //为查询“市场活动源”的超链接绑定单击事件
    $("#activitySourceA").click(function () {
        //弹出模态窗口
        $("#searchActivityModal").modal("show");
        //清空查询框中的内容
        $("#searchActivityT").val("");
        //查询已关联的所有市场活动
        queryActivityForConvertByActivityNameAndClueId("",clueId);
    });

    //为模态窗口的输入框绑定键盘弹起事件
    $("#searchActivityT").keyup(function () {
        //根据当前输入的内容查询
        queryActivityForConvertByActivityNameAndClueId(this.value,clueId);
    });

    //为单选按钮绑定单击事件
    //选择后修改外面的活动id和活动名
    $("#convert-tBody").on("click","input[type='radio']",function () {
        $("#activityId").val(this.value);
        $("#activityName").val($(this).attr("activityName"));
    });

    //为转换按钮绑定单击事件
    $("#saveConvertClueBtn").click(function () {
        //收集参数
        var money = $.trim($("#amountOfMoney").val());
        var name = $.trim($("#tradeName").val());
        var expectedDate = $("#expectedClosingDate").val();
        var stage = $("#stage").val();
        var activityId = $("#activityId").val();
        var isCreateTran = $("#isCreateTransaction").prop("checked");
        //表单验证
        if (isCreateTran) { //在需要新建交易的情况下才验证
            var moneyReg = /^(([1-9]\d*)|0)$/;
            if (money != "" && !moneyReg.test(money)) {
                alert("金额应为非负整数");
                return;
            }
        }

        //发送请求
        $.ajax({
            url : "workbench/clue/saveConvertClue.do",
            data : {
                clueId : clueId,
                money : money,
                name : name,
                expectedDate : expectedDate,
                stage : stage,
                activityId : activityId,
                isCreateTran : isCreateTran
            },
            type : "post",
            dataType : "json",
            success : function (data) {
                if (data.code == "1") {
                    //转换成功之后,跳转到线索主页面
                    alert("转换成功");
                    window.location.href = "workbench/clue/index.do";
                } else {
                    //转换失败,提示信息,页面不跳转
                    alert(data.message);
                }
            }
        })
    });


});

//根据活动名和线索id查询当前线索已关联的市场活动
queryActivityForConvertByActivityNameAndClueId = function (activityName, clueId) {
    //发送请求
    $.ajax({
        url : "workbench/clue/queryActivityForConvertByActivityNameAndClueId.do",
        data : {
            activityName : activityName,
            clueId : clueId
        },
        type : "post",
        dataType : "json",
        success : function (data) {
            //在关联市场活动的模态窗口中加载所有可关联的市场活动
            var activityList = "";
            $.each(data,function () {
                activityList += "<tr>";
                activityList += "<td><input type=\"radio\" name=\"activity\" activityName='"+this.name+"' value='"+this.id+"'/></td>";
                activityList += "<td>" + this.name + "</td>";
                activityList += "<td>" + this.startDate + "</td>";
                activityList += "<td>" + this.endDate + "</td>";
                activityList += "<td>" + this.owner + "</td>";
                activityList += "</tr>";
            });
            $("#convert-tBody").html(activityList);

            //选中已选中的那个市场活动(已选择的情况下再次点击超连接)
            var activityId = $("#activityId").val();
            if (activityId != "") {
                $("#convert-tBody input[value='"+activityId+"']").prop("checked",true);
            }
        }
    });

};