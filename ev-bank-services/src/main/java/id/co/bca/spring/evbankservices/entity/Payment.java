package id.co.bca.spring.evbankservices.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "account_from")
    private String accountFrom;

    @Column(name = "account_to")
    private String accountTo;

    @Column(name = "amount")
    private double amount;

    @Column(name = "tran_date")
    private Timestamp timestamp;

    @Column(name = "ref_no")
    private String refNo;

    @Column(name = "description")
    private String description;

}
