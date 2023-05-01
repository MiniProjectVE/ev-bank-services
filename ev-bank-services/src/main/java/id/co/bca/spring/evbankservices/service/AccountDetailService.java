package id.co.bca.spring.evbankservices.service;

import id.co.bca.spring.evbankservices.config.AccountDetail;
import id.co.bca.spring.evbankservices.entity.Account;
import id.co.bca.spring.evbankservices.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class AccountDetailService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> account = accountRepository.findByUsername(username);
        if (account.isEmpty()) throw new UsernameNotFoundException("Account not found");
        return new AccountDetail(account.orElse(null));
    }
}
