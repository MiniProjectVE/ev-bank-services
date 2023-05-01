package id.co.bca.spring.evbankservices.util;

import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FormatUtil {

    public static String doubleToStringFormat(double value) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMAN);
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        return decimalFormat.format(value);
    }

    public static Date stringToDateFormat(String strDate) {
        return Date.valueOf(strDate);
    }

    public static String dateToStringFormat(Date date) {
        String strDate = "1900-01-01";
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            strDate = dateFormat.format(date);
            return strDate;
        } catch (Exception e) {
            return strDate;
        }
    }

    public static String dateToStringFormatNoSpinal(Date date) {
        String strDate = "1900-01-01";
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            strDate = dateFormat.format(date);
            return strDate;
        } catch (Exception e) {
            return strDate;
        }
    }
}
