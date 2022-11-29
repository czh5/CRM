package com.heng.crm.commons.domain;

public class ReturnObject {
    private String code;    //返回的状态码，1为成功，0为失败
    private String message; //提示信息
    private Object retInf;  //其他信息

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getRetInf() {
        return retInf;
    }

    public void setRetInf(Object retInf) {
        this.retInf = retInf;
    }
}
