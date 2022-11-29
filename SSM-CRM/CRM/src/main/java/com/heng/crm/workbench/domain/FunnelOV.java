package com.heng.crm.workbench.domain;

public class FunnelOV {
    //在交易漏斗图中使用的实体类，分别保存 阶段的名称 以及 处于该阶段的交易数量
    private String name;
    private int value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
