package id.co.bca.spring.evbankservices.service;

import id.co.bca.spring.evbankservices.entity.Account;
import id.co.bca.spring.evbankservices.entity.AccountLog;
import id.co.bca.spring.evbankservices.repository.AccountLogRepository;
import id.co.bca.spring.evbankservices.repository.AccountRepository;
import id.co.bca.spring.evbankservices.util.DateUtil;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountLogRepository accountLogRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    public void deleteAccountById(int id) {
        accountRepository.deleteById(id);
    }

    public Account getAccountByCardNoAndPin(Account account) {
        Optional<Account> result = accountRepository.findAccountByCardNo(account.getCardNo());
        if(result.isPresent()) {
            boolean checkPw = passwordEncoder.matches(account.getPin(), result.get().getPin());
            return result.orElse(null);
        }
        return null;
    }

    public Account getAccountByAccountNo(String accountNo) {
        Optional<Account> result = accountRepository.findAccountByAccountNo(accountNo);
        return result.orElse(null);
    }

    @Transactional(rollbackOn = {Exception.class})
    public int debitBalanceByAccountNo(String accountNo, double debitValue, String description) {
        Account account = accountRepository.findAccountByAccountNo(accountNo).orElse(null);
        int result = 0; //assign 0 if account is not found
        if (account != null) {
            try {
                double newBalance = account.getBalance() - debitValue;
                if (newBalance >= 0) {
                    account.setBalance(newBalance);
                    accountRepository.save(account);

                    AccountLog postingLog = new AccountLog();
                    Timestamp timestamp = DateUtil.getTodayTimestamp();

                    postingLog.setAccountNo(account.getAccountNo());
                    postingLog.setId(1);
                    postingLog.setTranType("D");
                    postingLog.setTranDate(timestamp);
                    postingLog.setAmount(debitValue);
                    postingLog.setDescription(description == null ? "DEBIT NOTE" : description);

                    accountLogRepository.save(postingLog);

                    result = 1;
                } else {
                    result = 2; //insufficient balance
                }
            } catch (Exception e) {
                result = -1;
                e.printStackTrace();
            }
        }
        return result;
    }

    @Transactional(rollbackOn = {Exception.class})
    public int creditBalanceToAccountNo(String accountNo, double creditValue, String description) {
        Account account = accountRepository.findAccountByAccountNo(accountNo).orElse(null);
        int result = 0;
        if (account != null) {
            try {

                double newBalance = account.getBalance() + creditValue;
                account.setBalance(newBalance);
                accountRepository.save(account);

                AccountLog postingLog = new AccountLog();
                Timestamp timestamp = DateUtil.getTodayTimestamp();

                postingLog.setAccountNo(account.getAccountNo());
                postingLog.setTranType("D");
                postingLog.setTranDate(timestamp);
                postingLog.setAmount(creditValue);
                postingLog.setDescription(description == null ? "CREDIT NOTE" : description);

                accountLogRepository.save(postingLog);

                result = 1;
            } catch (Exception e) {
                result = -1;
                e.printStackTrace();
            }
        }
        return result;
    }

    public List<AccountLog> getAccountStatementByDate(String accountNo, String startDate, String endDate) {
        List<AccountLog> logs = null;
        try {
            Timestamp startDateTimeStamp = Timestamp.valueOf(startDate + " 00:00:00");
            Timestamp endDateTimeStamp = Timestamp.valueOf(endDate + " 23:59:59");
            logs = accountLogRepository.findAllAccountLogByAccountNoAndDate(accountNo, startDateTimeStamp, endDateTimeStamp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }
}
