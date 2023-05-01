package id.co.bca.spring.evbankservices.entity;

import id.co.bca.spring.evbankservices.model.PaymentDTO;
import id.co.bca.spring.evbankservices.util.FormatUtil;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Entity
@Data
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "crypto_trx_id")
    private int cryptoTranId;

    @Column(name = "account_from")
    private String accountFrom;

    @Column(name = "account_to")
    private String accountTo;

    @Column(name = "amount")
    private double amount;

    @Column(name = "tran_date")
    private Date tranDate;

    @Column(name = "ref_no", unique = true)
    private String refNo;

    @Column(name = "description")
    private String description;

    public static Payment fromDTO(PaymentDTO dto) {
        Payment payment = new Payment();
        payment.setId(Integer.parseInt(dto.getId()));
        payment.setCryptoTranId(Integer.parseInt(dto.getCryptoTranId()));
        payment.setAccountFrom(dto.getAccountFrom());
        payment.setAccountTo(dto.getAccountTo());
        payment.setAmount(Double.parseDouble(dto.getAmount()));
        payment.setTranDate(FormatUtil.stringToDateFormat(dto.getTranDate()));
        payment.setDescription(dto.getDescription());
        payment.setRefNo(dto.getRefNo());
        return payment;
    }

    public PaymentDTO toDTO() {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(String.valueOf(this.getId()));
        dto.setCryptoTranId(String.valueOf(this.getCryptoTranId()));
        dto.setAccountFrom(this.getAccountFrom());
        dto.setAccountTo(this.getAccountTo());
        dto.setAmount(FormatUtil.doubleToStringFormat(this.getAmount()));
        dto.setDescription(this.getDescription());
        dto.setTranDate(FormatUtil.dateToStringFormat(this.getTranDate()));
        dto.setRefNo(this.getRefNo());
        return dto;
    }
}
