$(function () {
    $.post("workbench/activity/loadUsername.do","",function (data) {
        //var username = "<option id='0' selected disabled></option>";
        var username = "";
        $.each(data,function (i,n) {
            username += "<option value='" + n.id + "'>" + n.name + "</option>";
        });
        $("#create-marketActivityOwner").html(username);
        $("#edit-marketActivityOwner").html(username);
    },"json");

    //为市场活动的创建按钮绑定事件
    $("#createActivityBtn").click(function () {
        //开启模态窗口前的处理
        //获取表单的dom对象，将表单重置
        $("#createActivityForm").get(0).reset();
        //开启模态窗口
        $("#createActivityModal").modal("show");
    })

    //为创建市场活动时的保存按钮绑定事件
    $("#saveCreateActivityBtn").click(function () {
        //收集数据
        var owner = $("#create-marketActivityOwner").val();
        var name = $.trim($("#create-marketActivityName").val());
        var startDate = $("#create-startDate").val();
        var endDate = $("#create-endDate").val();
        var cost = $.trim($("#create-cost").val());
        var description = $.trim($("#create-description").val());

        //表单验证
        if (owner == "") {
            alert("所有者不能为空");
            return;
        }
        if (name == "") {
            alert("名称不能为空");
            return;
        }
        if (startDate != "" && endDate != "") {
            if (startDate > endDate) {
                alert("结束日期不能比开始日期小");
                return;
            }
        }
        var regExp = /^(([1-9]\d*)|0)$/;
        if (!regExp.test(cost)) {
            alert("成本只能为非负整数");
            return;
        }

        //发送请求
        $.ajax({
            url : "workbench/activity/saveCreateActivity.do",
            data : {
                owner : owner,
                name : name,
                startDate : startDate,
                endDate : endDate,
                cost : cost,
                description : description
            },
            success : function (data) {
                if (data.code == "0") {
                    //创建失败,提示信息创建失败
                    alert(data.message);
                    //模态窗口不关闭,市场活动列表也不刷新
                    $("#createActivityModal").modal("show");    //可以不写
                } else {
                    //创建成功之后,关闭模态窗口
                    $("#createActivityModal").modal("hide");
                    //刷新市场活动列，显示第一页数据，保持每页显示条数不变
                    //容器.bs_pagination("getOption",属性) 可以获取翻页中的属性
                    queryActivityByConditionForPage(1,$("#page-control").bs_pagination("getOption","rowsPerPage"));
                }
            },
            type : "post",
            dataType : "json"
        });
    });

    //调用datatimepicker插件为开始时间和结束时间添加日历
    //参考官网：https://www.bootcss.com/p/bootstrap-datetimepicker/
    //为了不让用户手动更改只能点击，应将容器改为readonly
    //为了方便用户选中当天，添加todayBtn按钮
    //为了方便用户选中后清空，又因为是只读的，因此添加clearBtn按钮
    $(".date-control").datetimepicker({
        language : 'zh-CN',         //语言：中文
        format : 'yyyy-mm-dd',      //格式
        autoclose : true,           //选择完后自动关闭,默认为false
        minView : "month",          //最小时间视图，最小显示到月份的细节，即具体的一个月的每一天
        initialDate : new Date(),   //初始时间
        todayBtn : true,            //开启今天按钮，默认为false
        clearBtn : true             //开启清空按钮，默认为false
    });

    //当市场活动主页面加载完成之后,显示所有数据的第一页,默认显示10条
    queryActivityByConditionForPage(1,10);

    //用户在市场活动主页面填写查询条件,点击"查询"按钮,显示所有符合条件的数据的第一页，保持每页显示条数不变
    $("#queryActivityBtn").click(function () {
        //容器.bs_pagination("getOption",属性) 可以获取翻页中的属性
        queryActivityByConditionForPage(1,$("#page-control").bs_pagination("getOption","rowsPerPage"));
    });

    //给“全选”的checkbox添加单击事件
    $("#checkAll").click(function () {
        //tBody下的所有checkbox类型的input标签的checked属性都要与其同步
        //【>】表示父下面的一级子标签，【 】表示父下面的所有标签
        $("#tBody input[type='checkbox']").prop("checked",this.checked);
    });

    //当下方的checkbox全部选中时，“全选”的checkbox也要选中
    //当下方的checkbox至少有一个未选中时，“全选”的checkbox也不能选中
    //选择器.事件类型(function(){})只能为固有元素绑定事件，不能为动态元素绑定事件
    //父选择器.on(事件类型,子选择器,function(){})既可以为固有元素绑定事件，也可以为动态元素绑定事件
    //其中的父选择器可以是直接父选择器，也可以是间接父选择器
    $("#tBody").on("click","input[type='checkbox']",function () {
        //当tBody中全部复选框的数量与全部已选中的复选框数量相等时，“全选”的checkbox应选中
        if ($("#tBody input[type='checkbox']").size() ==
                $("#tBody input[type='checkbox']:checked").size()) {
            $("#checkAll").prop("checked",true);
        } else {    //否则若至少有一个未选中则全选的checkbox也不选中
            $("#checkAll").prop("checked",false);
        }
    });

    //为删除按钮绑定单击事件
    $("#deleteActivityBtn").click(function () {
        //获取所有已选中的记录
        var checkedIds = $("#tBody input[type='checkbox']:checked");
        //判断是否为空
        if (checkedIds.size() == 0) {
            alert("请选择至少一条需要删除的活动记录");
            return;
        }
        //确认是否删除
        if (confirm("是否确认删除")) {
            //获取所有复选框的value值
            var ids = "";
            $.each(checkedIds,function () {  //id=xxx&id=xxx&id=xxx&id=xxx&
                ids += "id=" + this.value + "&";
            });
            ids = ids.substr(0,ids.length-1);   //id=xxx&id=xxx&id=xxx&id=xxx
            //发送请求
            $.ajax({
                url : "workbench/activity/deleteActivityByIds.do",
                data : ids,
                type : "post",
                dataType : "json",
                success : function (data) {
                    if (data.code == "1") {
                        //删除成功之后,刷新市场活动列表,显示第一页数据,保持每页显示条数不变
                        queryActivityByConditionForPage(1,$("#page-control").bs_pagination("getOption","rowsPerPage"));
                    } else {
                        //删除失败,提示信息,列表不刷新
                        alert(data.message);
                    }
                }
            })
        }
    })

    //为修改按钮绑定单击事件
    $("#editActivityBtn").click(function () {
        //获取要修改的市场活动
        var editActivity = $("#tBody input[type='checkbox']:checked");
        //一次只能修改一条
        if (editActivity.size() == 0) {
            alert("请选择要修改的市场活动记录");
            return;
        }
        if (editActivity.size() > 1) {
            alert("每次只能修改一条市场活动记录");
            return;
        }
        //发送请求
        $.ajax({
            url : "workbench/activity/selectActivityById.do",
            data : {
                id : editActivity.val()
            },
            type : "post",
            dataType : "json",
            success : function (data) {
                //为模态窗口设置数据
                //添加隐藏窗口，方便后期收集数据
                $("#edit-id").val(data.id);
                //直接为下拉列表赋值，浏览器会自动选中val对应的选项
                $("#edit-marketActivityOwner").val(data.owner);
                $("#edit-marketActivityName").val(data.name);
                $("#edit-startDate").val(data.startDate);
                $("#edit-endDate").val(data.endDate);
                $("#edit-cost").val(data.cost);
                $("#edit-description").val(data.description);

                //弹出模态窗口
                $("#editActivityModal").modal("show");
            }
        })
    })

    //为修改市场活动时的更新按钮添加单击事件
    $("#updateActivityBtn").click(function () {
        //收集数据
        var id = $("#edit-id").val();
        var owner = $.trim($("#edit-marketActivityOwner").val());
        var name = $("#edit-marketActivityName").val();
        var startDate = $("#edit-startDate").val();
        var endDate = $("#edit-endDate").val();
        var cost = $.trim($("#edit-cost").val());
        var description = $.trim($("#edit-description").val());

        //表单验证
        if (owner === "") {
            alert("所有者不能为空");
            return;
        }
        if (name === "") {
            alert("名称不能为空");
            return;
        }
        if (startDate != "" && endDate != "") {
            if (startDate > endDate) {
                alert("结束日期不能比开始日期小");
                return;
            }
        }
        var regExp = /^(([1-9]\d*)|0)$/;
        if (!regExp.test(cost)) {
            alert("成本只能为非负整数");
            return;
        }

        //发送请求
        $.ajax({
            url : "workbench/activity/saveEditActivity.do",
            data : {
              id : id,
              owner : owner,
              name : name,
              startDate : startDate,
              endDate : endDate,
              cost : cost,
              description : description
            },
            type : "post",
            dataType : "json",
            success : function (data) {
                if (data.code == "1") {
                    //修改成功之后,关闭模态窗口,刷新市场活动列表,保持页号和每页显示条数都不变
                    $("#editActivityModal").modal("hide");
                    queryActivityByConditionForPage($("#page-control").bs_pagination("getOption","currentPage"),
                                                    $("#page-control").bs_pagination("getOption","rowsPerPage"));
                } else {
                    //修改失败,提示信息,模态窗口不关闭,列表也不刷新
                    alert(data.message);
                    $("#editActivityModal").modal("show");
                }
            }
        })
    });

    //为“批量导出”按钮添加单击事件
    $("#exportActivityAllBtn").click(function () {
        if (confirm("是否要批量导出市场活动")) {
            //使用同步请求，虽然是全局刷新，但是是在下载文件的窗口中刷新，因此不会影响其他，看起来还是局部刷新
            window.location.href = "workbench/activity/exportAllActivity.do";
        }

    });

    //为“选择导出”按钮添加单击事件
    $("#exportActivitySomeBtn").click(function () {
        if (confirm("是否要导出所选的市场活动")) {
            var activities = $("#tBody input[type='checkbox']:checked");
            if (activities.size() == 0) {
                alert("请至少选择一条需要导出的市场活动记录");
                return;
            }
            //获取所有要导出的市场活动id
            var ids = "";
            $.each(activities,function () { //id=xxx&id=xxx&id=xxx&id=xxx&
                ids += "id=" + this.value + "&";
            });
            ids = ids.substr(0,ids.length-1);   //id=xxx&id=xxx&id=xxx&id=xxx
            //发送同步请求
            window.location.href = "workbench/activity/exportSomeActivityByIds.do?" + ids;
        }
    });

    //为下载模板按钮绑定单击事件
    $("#downloadMouldBtn").click(function () {
        window.location.href = "workbench/activity/downloadMould.do";
    });

    //为导入活动按钮添加单击事件
    $("#importActivityBtn").click(function () {
        //获取文件
        var file = $("#activityFile")[0].files[0];
        //获取文件名
        var fileName = $("#activityFile").val();
        //获取文件后缀，从【.】开始到结尾，全部转成小写
        var suffix = fileName.substr(fileName.lastIndexOf(".") + 1).toLocaleLowerCase();

        //验证文件合法性
        if (suffix != "xls") {
            alert("仅支持后缀名为XLS的文件");
            return;
        }
        if (file.size > 1024*1024*5) {
            alert("请确认您的文件大小不超过5MB");
            return;
        }

        //发送请求
        /**
         *  在传递数据时，url传参和json传参都只能传递文本数据；而文件属于二进制数据，
         *  因此要用FormData来传递，它既可以传递文本数据又可以传递二进制数据
         *  FormData是ajax的接口，可以模拟键值对向后台传递参数
         *  使用formData要创建对象，使用append()方法添加数据
         *
         *  默认情况下，ajax发送请求也会进行urlencoded编码，将数据都转为字符串发往后台，
         *  而二进制数据无法转为字符串，因此会报错。需要添加两个设置：
         *  1、processData 设置ajax向后台提交数据之前，是否把数据统一转换为字符串。默认为true
         *  2、contentType 设置ajax向后台提交数据之前，是否把所有参数都统一按urlencoded编码。默认为true
         */

        var formData = new FormData();
        formData.append("activityFile",file);   //名称要与后台接收时一致
        $.ajax({
            url : "workbench/activity/importActivity.do",
            data : formData,
            processData : false,
            contentType : false,
            type : "post",
            dataType : "json",
            success : function (data) {
                if (data.code == "1") {
                    //导入成功之后,提示成功导入记录条数,关闭模态窗口,刷新市场活动列表,显示第一页数据,保持每页显示条数不变
                    alert("成功导入【" + data.retInf + '】条数据');
                    $("#importActivityModal").modal("hide");
                    queryActivityByConditionForPage(1,$("#page-control").bs_pagination("getOption","rowsPerPage"));
                } else {
                    //导入失败,提示信息,模态窗口不关闭,列表也不刷新
                    alert(data.message);
                    $("#importActivityModal").modal("show");
                }
            }
        })
    })
});

//根据条件分页查询市场活动
queryActivityByConditionForPage = function (pageNo, pageSize) {
    //获取数据用于分页查询，用于查询可以不用trim()
    var name = $("#query-name").val();
    var owner = $("#query-owner").val();
    var startDate = $("#query-startDate").val();
    var endDate = $("#query-endDate").val();
    //var pageNo = 1;
    //var pageSize = 10;

    $.ajax({
        url : "workbench/activity/queryActivityByConditionForPage.do",
        data : {
            name : name,
            owner : owner,
            startDate : startDate,
            endDate : endDate,
            pageNo : pageNo,
            pageSize : pageSize
        },
        type : "post",
        dataType : "json",
        success : function (data) {
            //展示总记录条数
            //使用了插件，就没有这部分了
            //$("#totalRowsB").text(data.totalRows);

            //展示每一条记录
            var activities = "";
            $.each(data.activityList,function (index,obj) {
                activities += "<tr class=\"active\">";
                activities += "<td><input type=\"checkbox\" value=\"" + obj.id + "\"/></td>";
                activities += "<td><a style=\"text-decoration: none; cursor: pointer;\" " +
                    "onclick=\"window.location.href='workbench/activity/toDetail.do?activityId="+ obj.id +"'\">" + obj.name + "</a></td>";
                activities += "<td>" + obj.owner + "</td>";
                activities += "<td>" + obj.startDate + "</td>";
                activities += "<td>" + obj.endDate + "</td>";
                activities += "</tr>";
            });
            $("#tBody").html(activities);

            //重置“全选”的复选框
            $("#checkAll").prop("checked",false);

            //对容器调用bs_pagination工具函数，显示翻页信息
            $("#page-control").bs_pagination({
                currentPage : pageNo,       //当前页，相当于pageNo
                rowsPerPage : pageSize,     //每页行数，相当于pageSize
                totalRows : data.totalRows, //总记录条数
                totalPages : Math.ceil(data.totalRows/pageSize), //总页数，必填项
                visiblePageLinks : 5,       //最多显示的卡片数
                showGoToPage : true,        //是否显示【跳转到】部分，默认为true
                showRowsPerPage : true,     //是否显示【每页条数】部分，默认为true
                showRowsInfo : true,        //是否显示【记录信息】部分，默认为true

                //用户每次切换页号都会自动调用该函数
                //该函数返回切换页号之后的pageNo和pageSize
                onChangePage : function (event,pageObj) {
                    //event表示翻页这个事件，pageObj表示这个翻页对象
                    queryActivityByConditionForPage(pageObj.currentPage,pageObj.rowsPerPage);
                }
            });
        }
    });
};
