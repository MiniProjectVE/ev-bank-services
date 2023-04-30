package id.co.bca.spring.evbankservices.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "account_log")
public class AccountLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "tran_type")
    private String tranType;

    @Column(name = "tran_date")
    private Timestamp tranDate;

    @Column(name = "amount")
    private double amount;

    @Column(name = "description")
    private String description;
}
