package com.heng.crm.commons.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private DateUtils() {}

    /**
     * 将日期进行格式化: yyyy-MM-dd hh:mm:ss
     * @param date
     * @return
     */
    public static String formatDatetime(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
    }
}
