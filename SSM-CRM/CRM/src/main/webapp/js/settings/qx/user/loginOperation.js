$(function () {
    //给整个窗口绑定键盘按下事件
    $(window).keydown(function (e) {
        if (e.keyCode == 13) {
            //按下回车进行登陆验证
            $("#loginBtn").click();
        }
    });


    //给登陆按钮绑定单击事件
    $("#loginBtn").click(function () {
        //获取用户名、密码和是否记住密码
        var loginAct = $.trim($("#loginAct").val());
        var loginPwd = $.trim($("#loginPwd").val());
        var isRemPwd = $("#isRemPwd").prop("checked");

        //表单验证
        if (loginAct == "" || loginPwd == "") {
            alert("账号或密码不能为空");
            return;
        }

        //验证账号密码的正确性
        var param = {
            loginAct : loginAct,
            loginPwd : loginPwd,
            isRemPwd : isRemPwd
        };

        /*$.post("settings/qx/user/login.do",param,function (data) {
            if (data.code == "1") {
                //登陆成功
                window.location.href = "workbench/index.do";
            } else {
                $("#msg").html(data.message);
            }
        },"json");*/

        $.ajax({
            url : "settings/qx/user/login.do",
            type : "post",
            data : param,
            dataType : "json",
            success : function (data) {
                if (data.code == "1") {
                    //登陆成功
                    window.location.href = "workbench/index.do";
                } else {
                    $("#msg").html(data.message);
                }
            },
            beforeSend : function () {
                //在ajax发送请求之前执行，该函数有返回值true/false
                //返回true则ajax会真正发送请求，返回false则不发送请求
                //该函数通常是做请求之前的验证
                //跟放在ajax请求之前是一样的效果，执行时机相近，但放在前面更早一点
                $("#msg").text("登陆验证中...");
                return true;
            }
        });
    });

    //若用户上次登陆时已记住密码，则自动填写
    var userLoginAct = getCookie("loginAct");
    var userLoginPwd = getCookie("loginPwd");
    if (userLoginAct != "" && userLoginPwd != "") {
        $("#loginAct").val(userLoginAct);
        $("#loginPwd").val(userLoginPwd);
        $("#isRemPwd").prop("checked",true);
    }

});

// 获取指定名称的cookie
function getCookie(name){
    var arrCookie = document.cookie.split("; ");//获取cookie字符串并分割
    //遍历匹配
    for ( var i = 0; i < arrCookie.length; i++) {
        var arr = arrCookie[i].split("=");
        if (arr[0] == name){
            return arr[1];
        }
    }
    return "";
}