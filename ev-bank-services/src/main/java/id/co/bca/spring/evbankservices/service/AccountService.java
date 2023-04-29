package id.co.bca.spring.evbankservices.service;

import id.co.bca.spring.evbankservices.entity.Account;
import id.co.bca.spring.evbankservices.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    public void deleteAccountById(int id) {
        accountRepository.deleteById(id);
    }

    public Account getAccountByCardNoAndPin(Account account) {
        Optional<Account> result = accountRepository.findAccountByCardNoAndPin(account.getCardNo(), account.getPin());
        return result.orElse(null);
    }

    public Account getAccountByAccountNo(String accountNo) {
        Optional<Account> result = accountRepository.findAccountByAccountNo(accountNo);
        return result.orElse(null);
    }

    public int debitBalanceByAccountNo(String accountNo, double debitValue) {
        Account account = accountRepository.findAccountByAccountNo(accountNo).orElse(null);
        int result = 0; //assign 0 if account is not found
        if (account != null) {
            double newBalance = account.getBalance() - debitValue;
            if (newBalance >= 0) {
                account.setBalance(newBalance);
                accountRepository.save(account);
                result = 1;
            } else {
                result = 2; //insufficient balance
            }
        }
        return result;
    }

    public int creditBalanceToAccountNo(String accountNo, double creditValue) {
        Account account = accountRepository.findAccountByAccountNo(accountNo).orElse(null);
        int result = 0;
        if (account != null) {
            double newBalance = account.getBalance() + creditValue;
            account.setBalance(newBalance);
            accountRepository.save(account);
            result = 1;
        }
        return result;
    }
}
