package id.co.bca.spring.evbankservices.entity.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true, value = {"code", "english", "indonesia"})
public class ResultEntity<T> {
    @Setter(AccessLevel.PRIVATE)
    private String code;
    @Setter(AccessLevel.PRIVATE)
    @JsonProperty("error_schema")
    @JsonAlias({"error_schema", "ErrorSchema", "error-schema"})
    private ErrorSchema errorSchema;
    @Setter(AccessLevel.PRIVATE)
    @JsonProperty("output_schema")
    @JsonAlias({"output_schema", "OutputSchema", "output-schema"})
    private T data;

    public ResultEntity(T data, VEErrorCode errorCode) {
        this.code = errorCode.toString().replace("_", "-");
        this.errorSchema = errorCode.getErrorSchema();
        this.data = data;
    }

    public ResultEntity(T data, VEErrorCode errorCode, Exception exception) {
        this.code = errorCode.toString().replace("_", "-");
        this.errorSchema = ErrorMessageBuilder.build(errorCode.toString().replace("_", "-"), exception.getCause().toString(),
                exception.getCause().toString());
        this.data = data;
    }

    public ResultEntity(T data, VEErrorCode errorCode, String customErrorEnglish, String customErrorIndonesia) {
        this.code = errorCode.toString().replace("_", "-");
        this.errorSchema = errorCode.getErrorSchema(customErrorEnglish, customErrorIndonesia);
        this.data = data;
    }

    public ResultEntity(T data, String errorCode) {
        this.code = errorCode.toUpperCase();
        this.errorSchema = ErrorMessageBuilder.build(errorCode.toString().toUpperCase());
        this.data = data;
    }

    public ResultEntity(T data, String errorCode, Map<String, String> mapping) {
        this.code = errorCode.toUpperCase();
        this.errorSchema = ErrorMessageBuilder.build(errorCode.toString().toUpperCase(), mapping);
        this.data = data;
    }

    public ResultEntity(T data, String errorCode, Exception exception) {
        this.code = errorCode.toUpperCase();
        this.errorSchema = ErrorMessageBuilder.build(errorCode.toString(), exception.getCause().toString(), exception.getCause().toString());
        this.data = data;
    }

    public ResultEntity(T data, String errorCode, String customErrorEnglish, String customErrorIndonesia) {
        this.code = errorCode.toUpperCase();
        this.errorSchema = ErrorMessageBuilder.build(errorCode.toString().toUpperCase(), customErrorEnglish, customErrorIndonesia);
        this.data = data;
    }

    public ResultEntity(T data, Exception exception) {
        this.code = "EXCEPTION";
        String message = exception.getMessage();
        message = (message != null && message != "") ? message : exception.getCause().toString();
        this.errorSchema = ErrorMessageBuilder.build("EXCEPTION", message, message);
        this.data = null;
    }

    public ResultEntity(Exception exception) {
        this.code = "EXCEPTION";
        String message = exception.getMessage();
        message = (message != null && message != "") ? message : exception.getCause().toString();
        this.errorSchema = ErrorMessageBuilder.build("EXCEPTION", message, message);
        this.data = null;
    }

    private ResultEntity(T data, String errorCode, ErrorSchema errorSchema) {
        this.code = errorCode.toUpperCase();
        this.errorSchema = errorSchema;
        this.data = data;
    }

    private ResultEntity<T> getResult() {
        return new ResultEntity<T>(this.data, this.code, this.errorSchema);
    }

    public ResponseEntity<?> SetHttpStatus(HttpStatus httpStatus) {
        return new ResponseEntity<Object>(this.getResult(), httpStatus);
    }

    public ResponseEntity<?> SetHttpHeadersAndStatus(HttpHeaders headers, HttpStatus httpStatus) {
        if (headers.size() > 0) {
            return new ResponseEntity<Object>(this.getResult(), headers, httpStatus);
        } else {
            return new ResponseEntity<Object>(this.getResult(), httpStatus);
        }

    }
}