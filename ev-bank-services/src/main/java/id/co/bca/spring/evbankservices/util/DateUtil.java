package id.co.bca.spring.evbankservices.util;

import java.sql.Timestamp;

public class DateUtil {
    public static Timestamp getTodayTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
