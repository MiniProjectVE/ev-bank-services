package id.co.bca.spring.evbankservices.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtil {

    public static String doubleFormatToString(double value) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMAN);
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        return decimalFormat.format(value);
    }
}
