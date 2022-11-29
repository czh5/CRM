$(function () {
    //定义全局变量保存用户信息
    var user;

    $.ajax({
        type : "post",
        url : "workbench/getUserInf.do",
        success : function (data) {
            user = data;
            //为首页的用户名赋值
            $("#username").html(user.name);
        },
        dataType : "json"
    });


});