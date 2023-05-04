package id.co.bca.spring.evbankservices.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "crypto_trx")
public class CryptoTrxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "wallet_id")
    private String walletId;
    @Column(name = "crypto_id")
    private String cryptoId;
    @Column(name = "crypto_amount")
    private double cryptoAmount;
    @Column(name = "total_amount")
    private double totalAmount;
    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "payment_status")
    private int paymentStatus;
    @Column(name = "transaction_date")
    private Date transactionDate;
    @Column(name = "account_no")
    private String accountNo;
    @Column(name = "card_no")
    private String cardNo;
}
