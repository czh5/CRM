$(function () {
    //全局变量，保存当前正在查看的那个市场活动信息
    var aDetail;

    //在页面加载完毕后加载数据
    $.ajax({
        url : "workbench/activity/loadActivityDetailAndRemark.do",
        data : {
            activityId : window.location.search.split("=")[1]
        },
        type : "post",
        dataType : "json",
        success : function (data) { //渲染页面
            //市场活动明细部分
            aDetail = data.activity;
            //大标题
            $("#title").html("<h3>市场活动-"+aDetail.name+" <small>"+aDetail.startDate+" ~ "+aDetail.endDate+"</small></h3>");
            //详细信息---所有者、名称、开始日期、结束日期、成本、创建者、创建时间、修改者、修改时间、描述
            $("#ownerB").text(aDetail.owner);
            $("#nameB").text(aDetail.name);
            $("#startDateB").text(aDetail.startDate);
            $("#endDateB").text(aDetail.endDate);
            $("#costB").text(aDetail.cost);
            $("#createByB").html(aDetail.createBy+"&nbsp;&nbsp;");
            $("#createTime").text(aDetail.createTime);
            $("#editByB").html(aDetail.editBy == null ? "无" :(aDetail.editBy+"&nbsp;&nbsp;"));
            $("#editTime").text(aDetail.editTime == null ? "" : aDetail.editTime);
            $("#description").text(aDetail.description);

            //备注部分
            var remark;
            $("#saveCreateActivityRemarkBtn").attr("activityId",aDetail.id);
            $("#remarksDiv").empty();
            $.each(data.activityRemarkList,function () {
                remark = "";
                remark += "<div class='remarkDiv' id='div_"+this.id+"' style='height: 60px;'>";
                remark += "<img title='"+(this.editFlag=="0" ? this.createBy : this.editBy)+"' src='image/user-thumbnail.png' style='width: 30px; height:30px;'>";
                remark += "<div style='position: relative; top: -40px; left: 40px;'>";
                remark += "<h5>"+this.noteContent+"</h5>";
                remark += "<font color='gray'>市场活动</font> <font color='gray'>-</font> <b>"+aDetail.name+"</b> <small style='color: gray;'>";
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
        }
    });

    //为添加备注时的保存按钮绑定单击事件
    $("#saveCreateActivityRemarkBtn").click(function () {
        //获取市场活动id和备注内容
        var activityId = $("#saveCreateActivityRemarkBtn").attr("activityId");
        var noteContent = $.trim($("#remark").val());

        if (noteContent == "") {
            alert("备注不能为空");
            return;
        }

        //发送请求
        $.ajax({
            url : "workbench/activity/saveCreateActivityRemark.do",
            data : {
              activityId : activityId,
              noteContent : noteContent
            },
            type : "post",
            dataType : "json",
            success : function (data) {
                if (data.code == "0") {
                    //添加失败,提示信息,输入框不清空,列表也不刷新
                    alert(data.message);
                } else {
                    //添加成功之后,清空输入框,刷新备注列表
                    $("#remark").val("");

                    var ar = data.retInf;
                    var newRemark = "";
                    newRemark += "<div class='remarkDiv' id='div_"+ar.id+"' style='height: 60px;'>";
                    newRemark += "<img title='"+ar.createBy+"' src='image/user-thumbnail.png' style='width: 30px; height:30px;'>";
                    newRemark += "<div style='position: relative; top: -40px; left: 40px;'>";
                    newRemark += "<h5>"+ar.noteContent+"</h5>";
                    newRemark += "<font color='gray'>市场活动</font> <font color='gray'>-</font> <b>"+aDetail.name+"</b> <small style='color: gray;'>";
                    newRemark += ar.createTime+" 由"+ar.createBy+"创建</small>";
                    newRemark += "<div style='position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;'>";
                    newRemark += "<a class='myHref' href='javascript:void(0);'name='editA' remarkId='"+ar.id+"'>" +
                        "<span class='glyphicon glyphicon-edit' style='font-size: 20px; color: #E6E6E6;'></span></a>";
                    newRemark += "&nbsp;&nbsp;&nbsp;&nbsp;";
                    newRemark += "<a class='myHref' href='javascript:void(0);' name='deleteA' remarkId='"+ar.id+"'>" +
                        "<span class='glyphicon glyphicon-remove' style='font-size: 20px; color: #E6E6E6;'></span></a>";
                    newRemark += "</div>";
                    newRemark += "</div>";
                    newRemark += "</div>";
                    $("#remarksDiv").append(newRemark);
                }
            }
        })
    });

    //为备注的删除按钮绑定单击事件，为动态元素绑定用on
    $("#remarksDiv").on("click","a[name='deleteA']",function () {
        //获取当前选中备注的id
        var id = $(this).attr("remarkId");
        //发送请求
        $.ajax({
            url : "workbench/activity/deleteActivityRemarkById.do",
            data : {
                id : id
            },
            type : "post",
            dataType : "json",
            success : function (data) {
                if (data.code == "1") {
                    //删除成功之后,刷新备注列表
                    //删除当前备注所在的div
                    $("#div_" + id).remove();
                } else {
                    //删除失败,提示信息,备注列表不刷新
                    alert(data.message);
                }
            }
        })
    });

    //为备注的修改按钮绑定单击事件
    $("#remarksDiv").on("click","a[name='editA']",function () {
        //获取备注id和备注内容
        var id = $(this).attr("remarkId");
        var noteContent = $("#div_" + id + " h5").text();
        //设置模态窗口的内容
        $("#edit_id").val(id);
        $("#edit_noteContent").val(noteContent);
        //显示模态窗口
        $("#editRemarkModal").modal("show");
    });

    //为修改备注时的确定按钮绑定单击事件
    $("#updateRemarkBtn").click(function () {
        //获取id和备注内容
        var id = $("#edit_id").val();
        var noteContent = $.trim($("#edit_noteContent").val());
        //验证内容合法性
        if (noteContent == "") {
            alert("备注内容不能为空");
            return;
        }
        //发送请求
        $.ajax({
            url : "workbench/activity/saveEditActivityRemark.do",
            data : {
                id : id,
                noteContent : noteContent
            },
            "type" : "post",
            "dataType" : "json",
            success : function (data) {
                if (data.code == "1") {
                    //修改成功之后,关闭模态窗口,刷新备注列表
                    $("#editRemarkModal").modal("hide");
                    $("#div_" + id + " h5").text(data.retInf.noteContent);
                    $("#div_" + id + " small").text(data.retInf.editTime + " 由" + data.retInf.editBy + "修改");
                } else {
                    //修改失败,提示信息,模态窗口不关闭,列表也不刷新
                    alert(data.message);
                    $("#editRemarkModal").modal("show");
                }
            }
        })
    })
});