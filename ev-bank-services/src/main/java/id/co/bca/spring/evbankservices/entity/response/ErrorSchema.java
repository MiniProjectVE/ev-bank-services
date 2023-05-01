package id.co.bca.spring.evbankservices.entity.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorSchema {

    @JsonProperty(value = "error_code", required = true)
    @JsonAlias({ "ErrorCode", "error_code", "error-code" })
    private String errorCode;
    @JsonProperty(value = "error_message", required = true)
    @JsonAlias({ "ErrorMessage", "error_message", "error-message" })
    private ErrorMessage errorMessage;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class ErrorMessage {
        @JsonProperty(value = "english", required = true)
        @JsonAlias({ "English", "english" })
        private String english;

        @JsonProperty(value = "indonesian", required = true)
        @JsonAlias({ "Indonesian", "indonesian" })
        private String indonesian;
    }
}