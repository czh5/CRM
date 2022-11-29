$(function () {
    var tranId = window.location.search.split("=")[1];

    //加载交易明细页面的内容
    $.ajax({
        url : "workbench/transaction/loadDetail.do",
        data : {
            id : tranId
        },
        type : "post",
        dataType : "json",
        success : function (data) {
            //加载交易明细
            var tranDetail = data.tran;
            $("#detail-title").html("<h3>"+tranDetail.name+" " +
                                (tranDetail.money==null ? "&nbsp;" : "<small>￥"+tranDetail.money) +"</small></h3>");
            $("#detail-money").html(tranDetail.money==null ? "&nbsp;" : tranDetail.money);
            $("#detail-name").html(tranDetail.name);
            $("#detail-expectedDate").html(tranDetail.expectedDate);
            $("#detail-customerId").html(tranDetail.customerId);
            $("#detail-stage").html(tranDetail.stage);
            $("#detail-type").html(tranDetail.type==null ? "&nbsp;" : tranDetail.type);
            $("#detail-possibility").html(data.possibility+"%");
            $("#detail-source").html(tranDetail.source==null ? "&nbsp;" : tranDetail.source);
            $("#detail-activityId").html(tranDetail.activityId==null ? "&nbsp;" : tranDetail.activityId);
            $("#detail-contactsId").html(tranDetail.contactsId==null ? "&nbsp;" : tranDetail.contactsId);
            $("#detail-createBy").html(tranDetail.createBy+"&nbsp;&nbsp;");
            $("#detail-createTime").html(tranDetail.createTime);
            $("#detail-editBy").html(tranDetail.editBy==null ? "&nbsp;" : tranDetail.editBy+"&nbsp;&nbsp;");
            $("#detail-editTime").html(tranDetail.editTime==null ? "&nbsp;" : tranDetail.editTime);
            $("#detail-description").html(tranDetail.description==null ? "&nbsp;" : tranDetail.description);
            $("#detail-contactSummary").html(tranDetail.contactSummary==null ? "&nbsp;" : tranDetail.contactSummary);
            $("#detail-nextContactTime").html(tranDetail.nextContactTime==null ? "&nbsp;" : tranDetail.nextContactTime);

            //加载备注
            var tranRemarkList = data.tranRemarkList;
            $.each(tranRemarkList,function () {
                loadNewTranRemark(this,tranDetail);
            });

            //加载交易历史阶段
            var tranHistoryList = data.tranHistoryList;
            var allHistory;
            $.each(tranHistoryList,function () {
                allHistory = "";
                allHistory += "<tr>";
                allHistory += "<td>"+this.stage+"</td>";
                allHistory += "<td>"+(this.money==null ? "&nbsp;" : this.money)+"</td>";
                allHistory += "<td>"+this.expectedDate+"</td>";
                allHistory += "<td>"+this.createTime+"</td>";
                allHistory += "<td>"+this.createBy+"</td>";
                allHistory += "</tr>";
            });
            $("#tranHistory-tBody").html(allHistory);

            //显示交易阶段的图标
            var stageIcon = "阶段&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
            $.each(data.stageList, function () {
                if (tranDetail.stage == this.value) {
                    //如果交易当前所处阶段与遍历到的阶段名称相同，则显示灯泡
                    stageIcon += "<span class=\"glyphicon glyphicon-map-marker mystage\" data-toggle=\"popover\" data-placement=\"bottom\"" +
                                    "data-content=\""+this.value+"\" style=\"color: #90F790;\"></span>";
                } else if (tranDetail.orderNo > this.orderNo) {
                    //如果遍历到的阶段在当前阶段之前，则显示已完成
                    stageIcon += "<span class=\"glyphicon glyphicon-ok-circle mystage\" data-toggle=\"popover\" data-placement=\"bottom\"" +
                                    "data-content=\""+this.value+"\" style=\"color: #90F790;\"></span>";
                } else {
                    //显示未到
                    stageIcon += "<span class=\"glyphicon glyphicon-record mystage\" data-toggle=\"popover\" data-placement=\"bottom\"" +
                                    "data-content=\""+this.value+"\"></span>";
                }
                stageIcon += "-----------";
            });
            stageIcon += "<span class=\"closingDate\">"+tranDetail.expectedDate+"</span>";
            $("#tranStage").html(stageIcon);
        }
    });


});

//加载新的交易备注，可以是加载页面时调用，也可以是添加备注后调用
loadNewTranRemark = function(remark, tran) {
    var newRemark = "";
    newRemark += "<div class=\"remarkDiv\" style=\"height: 60px;\">";
    newRemark += "<img title=\""+(remark.editFlag=="0" ? remark.createBy : remark.editBy)+"\" src=\"image/user-thumbnail.png\" style=\"width: 30px; height:30px;\">";
    newRemark += "<div style=\"position: relative; top: -40px; left: 40px;\">";
    newRemark += "<h5>"+remark.noteContent+"</h5>";
    newRemark += "<font color=\"gray\">交易</font> <font color=\"gray\">-</font> <b>"+tran.name+"</b> <small style=\"color: gray;\">";
    newRemark += (remark.editFlag=="0" ? remark.createTime+" 由"+remark.createBy+"创建" : remark.editTime+" 由"+remark.editBy+"修改");
    newRemark += "</small>";
    newRemark += "<div style=\"position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;\">";
    newRemark += "<a class=\"myHref\" href=\"javascript:void(0);\">";
    newRemark += "<span class=\"glyphicon glyphicon-edit\" style=\"font-size: 20px; color: #E6E6E6;\"></span></a>";
    newRemark += "&nbsp;&nbsp;&nbsp;&nbsp;";
    newRemark += "<a class=\"myHref\" href=\"javascript:void(0);\">";
    newRemark += "<span class=\"glyphicon glyphicon-remove\" style=\"font-size: 20px; color: #E6E6E6;\"></span></a>";
    newRemark += "</div>";
    newRemark += "</div>";
    newRemark += "</div>";
    //追加新的备注
    $("#remarksDiv").append(newRemark);
};