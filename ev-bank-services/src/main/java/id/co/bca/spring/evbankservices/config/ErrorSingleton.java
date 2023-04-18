package id.co.bca.spring.evbankservices.config;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ErrorSingleton {

    private HashMap<String, String> errorMap;

    public ErrorSingleton() {
        this.errorMap = new HashMap<>();
    }

    public HashMap<String,String> getErrorMap(String errorCode) {
        return null;
    }
}
