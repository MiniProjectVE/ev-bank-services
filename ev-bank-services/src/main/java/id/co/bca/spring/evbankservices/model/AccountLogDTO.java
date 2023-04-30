package id.co.bca.spring.evbankservices.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class AccountLogDTO {

    @JsonIgnore
    private int id;

    @JsonProperty("account_no")
    private String accountNo;

    @JsonProperty("tran_type")
    private String tranType;

    @JsonProperty("tran_date")
    private Timestamp tranDate;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("description")
    private String description;
}
