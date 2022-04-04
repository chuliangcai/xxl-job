package com.xxl.job.admin.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author chuyuancheng
 */
public class DateTimeUtils {

    private DateTimeUtils() {
        //nothing
    }

    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String format(LocalDateTime time) {
        return time == null ? "" : DEFAULT_FORMATTER.format(time);
    }
}
