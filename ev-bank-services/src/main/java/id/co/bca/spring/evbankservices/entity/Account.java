package id.co.bca.spring.evbankservices.entity;

import id.co.bca.spring.evbankservices.model.AccountDTO;
import id.co.bca.spring.evbankservices.util.FormatUtil;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "account")
public class Account {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "account_no", unique = true)
    private String accountNo;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "balance")
    private double balance;

    @Column(name = "card_no", unique = true)
    private String cardNo;

    @Column(name = "pin")
    private String pin;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "is_closed", columnDefinition = "boolean default false")
    private boolean isClosed;

    public static Account fromDTO(AccountDTO dto) {
        Account account = new Account();
        account.setId(dto.getId());
        account.setAccountNo(dto.getAccountNo());
        account.setAccountName(dto.getAccountName());
        account.setPin(dto.getPin());
        account.setBalance(Double.parseDouble(dto.getBalance() == null ? "0" : dto.getBalance()));
        account.setCardNo(dto.getCardNo());
        account.setUsername(dto.getUsername());
        return account;
    }

    public AccountDTO toDTO() {
        AccountDTO dto = new AccountDTO();
        dto.setId(this.getId());
        dto.setAccountName(this.getAccountName());
        dto.setAccountNo(this.getAccountNo());
        dto.setCardNo(this.getCardNo());
        dto.setBalance(FormatUtil.doubleToStringFormat(this.getBalance()));
        dto.setPin(this.getPin());
        dto.setUsername(this.getUsername());
        return dto;
    }
}
