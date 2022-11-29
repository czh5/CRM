package com.heng.crm.commons.utils;

import java.util.UUID;

public class UUIDUtils {
    private UUIDUtils() {}

    public static String getUUID() {
        return UUID.randomUUID().toString().trim().replaceAll("-","");
    }
}
