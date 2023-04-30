package id.co.bca.spring.evbankservices.repository;

import id.co.bca.spring.evbankservices.entity.AccountLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface AccountLogRepository extends CrudRepository<AccountLog, Integer> {
    @Query(value = "SELECT * FROM account_log WHERE account_no = ?1 AND (tran_date >= ?2 AND tran_date <= ?3)", nativeQuery = true)
    List<AccountLog> findAllAccountLogByAccountNoAndDate(String accountNo, Timestamp startDate, Timestamp endDate);
}
