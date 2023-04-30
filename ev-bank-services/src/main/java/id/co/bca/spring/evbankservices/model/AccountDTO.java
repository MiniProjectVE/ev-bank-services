package id.co.bca.spring.evbankservices.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountDTO {
    @JsonProperty("id")
    private int id;

    @JsonProperty("account_no")
    private String accountNo;

    @JsonProperty("account_name")
    private String accountName;

    @JsonProperty("balance")
    private String balance;

    @JsonProperty("card_no")
    private String cardNo;

    @JsonProperty("pin")
    private String pin;

    @JsonProperty(value = "is_closed", defaultValue = "false")
    private String isClosed;
}
