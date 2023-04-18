package id.co.bca.spring.evbankservices.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class PaymentDTO {
    @JsonProperty("id")
    private int id;

    @JsonProperty("account_from")
    private String accountFrom;

    @JsonProperty("account_to")
    private String accountTo;

    @JsonProperty("amount")
    private double amount;

    @JsonProperty("tran_date")
    private Timestamp tranDate;

    @JsonProperty("ref_no")
    private String refNo;

    @JsonProperty("description")
    private String description;
}
