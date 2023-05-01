package id.co.bca.spring.evbankservices.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("crypto_trx_id")
    private String cryptoTranId;

    @JsonProperty("account_from")
    private String accountFrom;

    @JsonProperty("account_to")
    private String accountTo;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("tran_date")
    private String tranDate;

    @JsonProperty("ref_no")
    private String refNo;

    @JsonProperty("description")
    private String description;

}
