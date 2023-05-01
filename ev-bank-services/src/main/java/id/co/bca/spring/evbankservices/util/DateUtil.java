package id.co.bca.spring.evbankservices.util;

import java.sql.Date;
import java.sql.Timestamp;

public class DateUtil {
    public static Timestamp getTodayTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static Date getTodayDate() {
        return new Date(System.currentTimeMillis());
    }
}
