$(function () {
    //加载下拉列表中的内容
    $.ajax({
        url : "workbench/clue/loadSelectOption.do",
        type : "post",
        dataType : "json",
        success : function (data) {
            //传过来的data包含了四个集合
            var userList = data.userList;
            var appellationList = data.appellationList;
            var clueStateList = data.clueStateList;
            var sourceList = data.sourceList;

            //清空下拉列表，遍历四个集合，并将集合的内容放入下拉列表中
            $("#create-clueOwner").html("<option></option>");     //所有者
            $("#create-call").html("<option></option>");          //称呼
            $("#create-status").html("<option></option>");        //线索状态
            $("#create-source").html("<option></option>");        //线索来源
            $("#edit-clueOwner").html("<option></option>");         //所有者
            $("#edit-call").html("<option></option>");              //称呼
            $("#edit-status").html("<option></option>");            //线索状态
            $("#edit-source").html("<option></option>");            //线索来源
            $("#query-source").html("<option></option>");         //线索来源
            $("#query-status").html("<option></option>");         //线索状态

            $.each(userList,function () {
                $("#create-clueOwner").append("<option value='" + this.id + "'>" + this.name + "</option>");
                $("#edit-clueOwner").append("<option value='" + this.id + "'>" + this.name + "</option>");
            });

            $.each(appellationList,function () {
                $("#create-call").append("<option value='" + this.id + "'>" + this.value + "</option>");
                $("#edit-call").append("<option value='" + this.id + "'>" + this.value + "</option>");
            });

            $.each(clueStateList,function () {
                $("#create-status").append("<option value='" + this.id + "'>" + this.value + "</option>");
                $("#edit-status").append("<option value='" + this.id + "'>" + this.value + "</option>");
                $("#query-status").append("<option value='" + this.id + "'>" + this.value + "</option>");
            });

            $.each(sourceList,function () {
                $("#create-source").append("<option value='" + this.id + "'>" + this.value + "</option>");
                $("#edit-source").append("<option value='" + this.id + "'>" + this.value + "</option>");
                $("#query-source").append("<option value='" + this.id + "'>" + this.value + "</option>");
            });
        }
    });

    //初始时加载第一页的10条记录
    queryClueByConditionForPage(1,10);

    //为创建按钮绑定单击事件
    $("#createClueBtn").click(function () {
        $("#createClueModal").modal("show");
    });

    //为创建线索的保存按钮绑定单击事件
    $("#saveCreateClueBtn").click(function () {
        //收集参数
        var owner = $("#create-clueOwner").val();
        var company = $.trim($("#create-company").val());
        var appellation = $("#create-call").val();
        var fullname = $.trim($("#create-surname").val());
        var job = $.trim($("#create-job").val());
        var email = $.trim($("#create-email").val());
        var phone = $.trim($("#create-phone").val());
        var website = $.trim($("#create-website").val());
        var mphone = $.trim($("#create-mphone").val());
        var state = $("#create-status").val();
        var source = $("#create-source").val();
        var description = $.trim($("#create-description").val());
        var contactSummary = $.trim($("#create-contactSummary").val());
        var nextContactTime = $.trim($("#create-nextContactTime").val());
        var address = $.trim($("#create-address").val());
        //表单验证
        if (!checkFormOnCreateAndEdit(owner,company,fullname,email,phone,website,mphone)) {
            return;
        }
        //发送请求
        $.ajax({
            url : "workbench/clue/saveCreateClue.do",
            data : {
                owner : owner,
                company : company,
                appellation : appellation,
                fullname : fullname,
                job : job,
                email : email,
                phone : phone,
                website : website,
                mphone : mphone,
                state : state,
                source : source,
                description : description,
                contactSummary : contactSummary,
                nextContactTime : nextContactTime,
                address : address
            },
            type : "post",
            dataType : "json",
            success : function (data) {
                if (data.code == "1") {
                    //创建成功之后，关闭模态窗口，刷新线索列表，显示第一页数据，保持每页显示条数不变
                    $("#createClueModal").modal("hide");
                    queryClueByConditionForPage(1,$("#page-control").bs_pagination("getOption","rowsPerPage"));
                } else {
                    //创建失败，提示信息，模态窗口不关闭，列表也不刷新
                    alert(data.message);
                    $("#createClueModal").modal("show");
                }
            }
        })
    })


});

//根据条件分页查询线索
queryClueByConditionForPage = function (pageNo, pageSize) {
    //获取参数
    var fullName = $.trim($("#query-fullName").val());
    var company = $.trim($("#query-company").val());
    var phone = $.trim($("#query-phone").val());
    var source = $("#query-source").val();
    var owner = $.trim($("#query-owner").val());
    var mphone = $.trim($("#query-mphone").val());
    var state = $("#query-status").val();
    //发送请求
    $.ajax({
        url : "workbench/clue/queryClueByConditionForPage.do",
        data : {
            "fullName" : fullName,
            "company" : company,
            "phone" : phone,
            "source" : source,
            "owner" : owner,
            "mphone" : mphone,
            "state" : state,
            "pageNo" : pageNo,
            "pageSize" : pageSize
        },
        type : "post",
        dataType : "json",
        success : function (data) {
            var clues = "";
            $.each(data.clueList,function () {
                clues += "<tr>";
                clues += "<td><input type=\"checkbox\"/></td>";
                clues += "<td><a style=\"text-decoration: none; cursor: pointer;\" " +
                            "onclick=\"window.location.href='workbench/clue/toDetail.do?id="+this.id+"';\">"
                                + this.fullname + this.appellation + "</a></td>";
                clues += "<td>" + this.company + "</td>";
                clues += "<td>" + this.phone + "</td>";
                clues += "<td>" + this.mphone + "</td>";
                clues += "<td>" + this.source + "</td>";
                clues += "<td>" + this.owner + "</td>";
                clues += "<td>" + this.state + "</td>";
                clues += "</tr>";
            });
            $("#tBody").html(clues);

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
                    queryClueByConditionForPage(pageObj.currentPage,pageObj.rowsPerPage);
                }
            });
        }
    })
};

//在创建和修改线索时进行表单验证
checkFormOnCreateAndEdit = function (owner, company, fullName, email, phone, website, mPhone) {
    if (owner == "") {
        alert("所有者不能为空");
        return false;
    }
    if (company == "") {
        alert("公司不能为空");
        return false;
    }
    if (fullName == "") {
        alert("姓名不能为空");
        return false;
    }

    var emailReg = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
    var phoneReg = /\d{3}-\d{8}|\d{4}-\d{7}/;
    var websiteReg = /^(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*(\\?\\S*)?$/;
    var mPhoneReg = /^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$/;

    if (email != "" && !emailReg.test(email)) {
        alert("请按正确格式填写邮箱");
        return false;
    }
    if (phone != "" && !phoneReg.test(phone)) {
        alert("请按正确格式填写公司座机");
        return false;
    }
    if (website != "" && !websiteReg.test(website)) {
        alert("请按正确格式填写公司网站");
        return false;
    }
    if (mPhone != "" && !mPhoneReg.test(mPhone)) {
        alert("请按正确格式填写手机");
        return false;
    }
    //走到此处说明验证成功
    return true;
};