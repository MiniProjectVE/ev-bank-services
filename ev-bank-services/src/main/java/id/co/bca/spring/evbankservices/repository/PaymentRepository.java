package id.co.bca.spring.evbankservices.repository;

import id.co.bca.spring.evbankservices.entity.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;

public interface PaymentRepository extends CrudRepository<Payment, Integer> {
    @Query(value = "SELECT * FROM payment WHERE tran_date = ?1 ORDER BY tran_date,id DESC LIMIT 1", nativeQuery = true)
    Payment findTopByOrderByTranDateToday(Date today);
}
