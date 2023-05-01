package id.co.bca.spring.evbankservices.entity.response;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

@Component
public class ErrorMessageBuilder {
    private static ErrorSchema getErrorMessage(String code, String english, String indonesia,
                                               Map<String, String> mapping) {
        String en = "";
        String id = "";
        String errorCode = "";
        if (VEProjectInfo.getEnvironment().getProperty(code.toUpperCase() + ".EN") == null
                || VEProjectInfo.getEnvironment().getProperty(code.toUpperCase() + ".ID") == null) {
            en = "System unavailable at the moment. Please try again later.";
            id = "Sistem tidak dapat dipergunakan untuk sementara. Cobalah beberapa saat lagi.";
            errorCode = "VE-999";
//            BCALogger.write(BCALogType.INFO, "Mapping for Error Code : " + code + " Not Found.");
            System.out.println("Mapping for Error Code : " + code + " Not Found.");
        } else {
            en = VEProjectInfo.getEnvironment().getProperty(code.toUpperCase() + ".EN");
            id = VEProjectInfo.getEnvironment().getProperty(code.toUpperCase() + ".ID");
            errorCode = code;
        }

        if (mapping != null) {
            for (Map.Entry<String, String> item : mapping.entrySet()) {
                en = en.replaceAll("(?i)" + Pattern.quote("#" + item.getKey().trim() + "#"), item.getValue());
                id = id.replaceAll("(?i)" + Pattern.quote("#" + item.getKey().trim() + "#"), item.getValue());
            }
        }

        if (!en.equals("")) {
            String[] array = en.split(";");
            if (array.length > 1) {
                en = array[0].toString();
                errorCode = array[1].toString().toUpperCase();
            }
        }

        if (!id.equals("")) {
            String[] array = id.split(";");
            if (array.length > 1) {
                id = array[0].toString();
                errorCode = array[1].toString().toUpperCase();
            }
        }
        ErrorSchema errSchema = new ErrorSchema();
        ErrorSchema.ErrorMessage errMessage = errSchema.new ErrorMessage();
        errSchema.setErrorCode(errorCode);
        errMessage.setEnglish(english == null ? en : english);
        errMessage.setIndonesian(indonesia == null ? id : indonesia);
        errSchema.setErrorMessage(errMessage);
        return errSchema;
    }

    public static ErrorSchema build(String code) {
        return getErrorMessage(code, null, null, null);
    }

    public static ErrorSchema build(String code, Map<String, String> mapping) {
        return getErrorMessage(code, null, null, mapping);
    }

    public static ErrorSchema build(String code, String english, String indonesia) {
        return getErrorMessage(code, english, indonesia, null);
    }
}
