package id.co.bca.spring.evbankservices.repository;

import id.co.bca.spring.evbankservices.entity.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, Integer> {
    Optional<Account> findAccountByCardNoAndPin(String cardNo, String pin);

    Optional<Account> findAccountByAccountNo(String accountNo);
}