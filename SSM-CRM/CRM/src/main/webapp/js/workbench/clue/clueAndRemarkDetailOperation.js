$(function () {
    //当前线索的id
    var clueId = window.location.search.split("=")[1];

    //发送请求加载线索明细、备注、市场活动
    $.ajax({
        url : "workbench/clue/loadClueDetailAndRemark.do",
        data : {
            id : clueId
        },
        type : "post",
        dataType : "json",
        success : function (data) {
            var clueDetail = data.clue;   //线索明细
            var clueRemarkList = data.clueRemarkList; //备注
            var activityList = data.activityList;   //市场活动

            //加载线索明细
            $("#detail-title").html(clueDetail.fullname + clueDetail.appellation + "<small>" + clueDetail.company +  "</small>");
            $("#detail-name").text(clueDetail.fullname + clueDetail.appellation);
            $("#detail-owner").text(clueDetail.owner);
            $("#detail-company").text(clueDetail.company);
            $("#detail-job").text(clueDetail.job);
            $("#detail-email").text(clueDetail.email);
            $("#detail-phone").text(clueDetail.phone);
            $("#detail-website").text(clueDetail.website);
            $("#detail-mphone").text(clueDetail.mphone);
            $("#detail-state").text(clueDetail.state);
            $("#detail-source").text(clueDetail.source);
            $("#detail-createBy").html(clueDetail.createBy + "&nbsp;&nbsp;");
            $("#detail-createTime").text(clueDetail.createTime);
            $("#detail-editBy").html(clueDetail.editBy == null ? "无" : clueDetail.editBy + "&nbsp;&nbsp;");
            $("#detail-editTime").text(clueDetail.editTime == null ? "" : clueDetail.editTime);
            $("#detail-description").text(clueDetail.description);
            $("#detail-contactSummary").text(clueDetail.contactSummary);
            $("#detail-nextContactTime").text(clueDetail.nextContactTime);
            $("#detail-address").text(clueDetail.address);

            //加载备注
            var remark;
            $("#remarksDiv").empty();
            $.each(clueRemarkList,function () {
                remark = "";
                remark += "<div class='remarkDiv' id='div_"+this.id+"' style='height: 60px;'>";
                remark += "<img title='"+(this.editFlag=="0" ? this.createBy : this.editBy)+"' src='image/user-thumbnail.png' style='width: 30px; height:30px;'>";
                remark += "<div style='position: relative; top: -40px; left: 40px;'>";
                remark += "<h5>"+this.noteContent+"</h5>";
                remark += "<font color='gray'>线索</font> <font color='gray'>-</font> <b>"+clueDetail.fullname + clueDetail.appellation +"-"+ clueDetail.company+"</b> <small style='color: gray;'>";
                remark += (this.editFlag=="0" ? this.createTime : this.editTime)+" 由"+(this.editFlag=="0" ? this.createBy+"创建" : this.editBy+"修改")+"</small>";
                remark += "<div style='position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;'>";
                remark += "<a class='myHref' href='javascript:void(0);' name='editA' remarkId='"+this.id+"'>" +
                    "<span class='glyphicon glyphicon-edit' style='font-size: 20px; color: #E6E6E6;'></span></a>";
                remark += "&nbsp;&nbsp;&nbsp;&nbsp;";
                remark += "<a class='myHref' href='javascript:void(0);' name='deleteA' remarkId='"+this.id+"'>" +
                    "<span class='glyphicon glyphicon-remove' style='font-size: 20px; color: #E6E6E6;'></span></a>";
                remark += "</div>";
                remark += "</div>";
                remark += "</div>";
                $("#remarksDiv").append(remark);
            });

            //加载已与该线索关联的市场活动
            var activity;
            $("#clue-activity").empty();
            $.each(activityList,function () {
                activity = "";
                activity += "<tr id='tr_"+this.id+"'>";
                activity += "<td>" + this.name + "</td>";
                activity += "<td>" + this.startDate + "</td>";
                activity += "<td>" + this.endDate + "</td>";
                activity += "<td>" + this.owner + "</td>";
                activity += "<td><a href=\"javascript:void(0);\" activityId='"+this.id+"'" +
                                "style=\"text-decoration: none;\">" +
                                "<span class=\"glyphicon glyphicon-remove\"></span>解除关联</a></td>";
                activity += "</tr>";
                $("#clue-activity").append(activity);
            })
        }
    });

    //为“关联市场活动”的超链接绑定单击事件
    $("#relateActivityA").click(function () {
        //弹出关联市场活动的模态窗口
        $("#relateModal").modal("show");
        //清空模态窗口搜索框的内容
        $("#queryActivityT").val("");
        //显示当前可关联的所有市场活动
        queryActivityForDetailByActivityNameAndClueId("",clueId);
    });

    //在模态窗口中，当键盘弹起时，进行模糊查询
    $("#queryActivityT").keyup(function () {
        //用户在线索关联市场活动的模态窗口,输入搜索条件,每次键盘弹起,根据名称模糊查询市场活动,
        //把所有符合条件的市场活动显示到列表中
        queryActivityForDetailByActivityNameAndClueId(this.value, clueId);
    })

    //为模态窗口中的全选框添加单击事件
    $("#checkAll").change(function () {
        $("#relate-tBody input[type='checkbox']").prop("checked", $(this).prop("checked"));
    });

    //为模态窗口中的其他复选框添加单击事件
    $("#relate-tBody").on("change", "input[type='checkbox']", function () {
        var totalCheckBox = $("#relate-tBody input[type='checkbox']");
        var checkedCheckBox = $("#relate-tBody input[type='checkbox']:checked");
        if (totalCheckBox.size() == checkedCheckBox.size()) {
            //表示此时已全选，应将全选的checkbox也选上
            $("#checkAll").prop("checked",true);
        } else {
            $("#checkAll").prop("checked",false);
        }
    })

    //为模态窗口的关联按钮绑定单击事件
    $("#clueActivityRelationBtn").click(function () {
        var checkedCheckBox = $("#relate-tBody input[type='checkbox']:checked");
        if (checkedCheckBox.size() == 0) {
            alert("请至少选中一条需要关联的市场活动");
            return;
        }
        //封装数据
        var ret = "";
        $.each(checkedCheckBox,function () {    //id=xxx&id=xxx&id=xxx&id=xxx&
            ret += "id=" + this.value + "&"
        });
        ret += "clueId=" + clueId;
        //发送请求
        $.ajax({
            url : "workbench/clue/saveClueActivityRelationByList.do",
            data : ret,
            type : "post",
            dataType : "json",
            success : function (data) {
                if (data.code == "0") {
                    //关联失败,提示信息,模态窗口不关闭,已经关联过的市场活动列表也不刷新
                    alert(data.message);
                    $("#relateModal").modal("show");
                } else {
                    //关联成功之后,关闭模态窗口,刷新已经关联过的市场活动列表
                    $("#relateModal").modal("hide");

                    var activity = "";
                    $.each(data.retInf,function () {
                        activity += "<tr id='tr_"+this.id+"'>";
                        activity += "<td>" + this.name + "</td>";
                        activity += "<td>" + this.startDate + "</td>";
                        activity += "<td>" + this.endDate + "</td>";
                        activity += "<td>" + this.owner + "</td>";
                        activity += "<td><a href=\"javascript:void(0);\" activityId='"+this.id+"'" +
                            "style=\"text-decoration: none;\">" +
                            "<span class=\"glyphicon glyphicon-remove\"></span>解除关联</a></td>";
                        activity += "</tr>";
                    });
                    $("#clue-activity").append(activity);
                };
            }
        })
    })

    //为解除关联关系超链接绑定单击事件
    $("#clue-activity").on("click","a",function () {
        if (confirm("是否要删除该关联关系？")) {
            var activityId = $(this).attr("activityId");
            //发送请求
            $.ajax({
                url : "workbench/clue/deleteClueActivityRelationByClueIdAndActivityId.do",
                data : {
                    activityId : activityId,
                    clueId : clueId
                },
                type : "post",
                dataType : "json",
                success : function (data) {
                    if (data.code == "0") {
                        //解除失败,提示信息,列表也不刷新
                        alert(data.message);
                    } else {
                        //解除成功之后,刷新已经关联的市场活动列表
                        $("#clue-activity tr[id='tr_"+activityId+"']").remove();
                    }
                }
            })
        }
    });

    //为线索明细页面中的转换按钮绑定单击事件
    $("#convertBtn").click(function () {
        //同步请求
        window.location.href = "workbench/clue/toConvert.do?clueId=" + clueId;
    })


});

//根据活动名和线索id查询当前线索未关联的市场活动
queryActivityForDetailByActivityNameAndClueId = function (activityName, clueId) {
    //发送请求
    $.ajax({
        url : "workbench/clue/queryActivityForDetailByActivityNameAndClueId.do",
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
                activityList += "<td><input type=\"checkbox\" value='"+this.id+"'/></td>";
                activityList += "<td>" + this.name + "</td>";
                activityList += "<td>" + this.startDate + "</td>";
                activityList += "<td>" + this.endDate + "</td>";
                activityList += "<td>" + this.owner + "</td>";
                activityList += "</tr>";
            });
            $("#relate-tBody").html(activityList);
        }
    });

};