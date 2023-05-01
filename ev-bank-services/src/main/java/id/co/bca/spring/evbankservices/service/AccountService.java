package id.co.bca.spring.evbankservices.service;

import id.co.bca.spring.evbankservices.entity.Account;
import id.co.bca.spring.evbankservices.entity.AccountLog;
import id.co.bca.spring.evbankservices.entity.response.ResultEntity;
import id.co.bca.spring.evbankservices.model.AccountDTO;
import id.co.bca.spring.evbankservices.model.AccountLogDTO;
import id.co.bca.spring.evbankservices.repository.AccountLogRepository;
import id.co.bca.spring.evbankservices.repository.AccountRepository;
import id.co.bca.spring.evbankservices.util.DateUtil;
import id.co.bca.spring.evbankservices.util.FormatUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
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

    @Transactional(rollbackOn = Exception.class)
    public ResultEntity<AccountDTO> saveAccount(Account account) throws Exception {
        try {
            String encodedPassword = passwordEncoder.encode(account.getPin());
            account.setPin(encodedPassword);
            Account insertResult = accountRepository.save(account);
            AccountDTO dtoResult = insertResult.toDTO();
            return new ResultEntity<AccountDTO>(dtoResult, "VE-000");
        } catch (Exception e) {
            if(e.getMessage() == null || e.getMessage().isEmpty()) {
                throw new Exception("VE-999");
            } else {
                throw new Exception(e.getMessage());
            }
        }
    }

    public ResultEntity<HashMap<String, String>> getAccountByCardNoAndPin(Account account) throws Exception {
        try {
            String errorCode = "VE-000";
            HashMap<String, String> balanceResult = new HashMap<>();
            Optional<Account> result = accountRepository.findAccountByCardNo(account.getCardNo());
            if(result.isPresent()) {
                boolean checkPw = passwordEncoder.matches(account.getPin(), result.get().getPin());
                if(checkPw) {
                    balanceResult.put("account_no", result.get().getAccountNo());
                    balanceResult.put("balance", FormatUtil.doubleFormatToString(result.get().getBalance()));
                 } else {
                    balanceResult.put("account_no", "BAD CREDENTIALS");
                    balanceResult.put("balance", "0");
                    errorCode = "VE-022";
                }
            } else {
                balanceResult.put("account_no", "NOT FOUND");
                balanceResult.put("balance", "0");
                errorCode = "VE-102";
            }
            return new ResultEntity<HashMap<String, String>>(balanceResult, errorCode);
        } catch (Exception e) {
            if(e.getMessage() == null || e.getMessage().isEmpty()) {
                throw new Exception("VE-999");
            } else {
                throw new Exception(e.getMessage());
            }
        }
    }

    public ResultEntity<AccountDTO> getAccountByAccountNo(String accountNo) throws Exception {
        try {
            Optional<Account> account = accountRepository.findAccountByAccountNo(accountNo);
            AccountDTO dto = null;
            String errorCode = "VE-102";
            if (account.isPresent()) {
                dto = account.get().toDTO();
                dto.setPin("!CONFIDENTAL!"); //Hide pin
                errorCode = "VE-000";
            }
            return new ResultEntity<>(dto, errorCode);
        } catch (Exception e) {
            if(e.getMessage() == null || e.getMessage().isEmpty()) {
                throw new Exception("VE-999");
            } else {
                throw new Exception(e.getMessage());
            }
        }
    }

    @Transactional(rollbackOn = {Exception.class})
    public ResultEntity<HashMap<String, String>> debitBalanceByAccountNo(String accountNo, double debitValue, String description) throws Exception {
        try {
            HashMap<String, String> responseMap = new HashMap<>();
            Account account = accountRepository.findAccountByAccountNo(accountNo).orElse(null);
            if (account != null) {
                double newBalance = account.getBalance() - debitValue;
                if (newBalance >= 0) {
                    account.setBalance(newBalance);
                    accountRepository.save(account);

                    AccountLog postingLog = new AccountLog();
                    Timestamp timestamp = DateUtil.getTodayTimestamp();

                    postingLog.setAccountNo(account.getAccountNo());
                    postingLog.setTranType("D");
                    postingLog.setTranDate(timestamp);
                    postingLog.setAmount(debitValue);
                    postingLog.setDescription(description == null ? "DEBIT NOTE" : description);

                    accountLogRepository.save(postingLog);
                    responseMap.put("status", "Debit success");
                    return new ResultEntity<HashMap<String, String>>(responseMap, "VE-000");
                }
                responseMap.put("status", "Debit failed");
                return new ResultEntity<HashMap<String, String>>(responseMap, "VE-301");
            }
            responseMap.put("status", "Debit failed");
            return new ResultEntity<HashMap<String, String>>(responseMap, "VE-102");
        } catch (Exception e) {
            if(e.getMessage() == null || e.getMessage().isEmpty()) {
                throw new Exception("VE-999");
            } else {
                throw new Exception(e.getMessage());
            }
        }
    }

    @Transactional(rollbackOn = {Exception.class})
    public ResultEntity<HashMap<String, String>> creditBalanceToAccountNo(String accountNo, double creditValue, String description) throws Exception {
        try {
            HashMap<String, String> responseMap = new HashMap<>();
            Account account = accountRepository.findAccountByAccountNo(accountNo).orElse(null);
            if (account != null) {
                double newBalance = account.getBalance() + creditValue;
                account.setBalance(newBalance);
                accountRepository.save(account);

                AccountLog postingLog = new AccountLog();
                Timestamp timestamp = DateUtil.getTodayTimestamp();

                postingLog.setAccountNo(account.getAccountNo());
                postingLog.setTranType("D");
                postingLog.setTranDate(timestamp);
                postingLog.setAmount(creditValue);
                postingLog.setDescription(description == null ? "DEBIT NOTE" : description);

                accountLogRepository.save(postingLog);
                responseMap.put("status", "Credit success");
                return new ResultEntity<HashMap<String, String>>(responseMap, "VE-000");
            }
            responseMap.put("status", "Credit failed");
            return new ResultEntity<HashMap<String, String>>(responseMap, "VE-102");
        } catch (Exception e) {
            if(e.getMessage() == null || e.getMessage().isEmpty()) {
                throw new Exception("VE-999");
            } else {
                throw new Exception(e.getMessage());
            }
        }
    }

    public ResultEntity<List<AccountLogDTO>> getAccountStatementByDate(String accountNo, String startDate, String endDate) throws Exception {
        List<AccountLog> logs = null;
        String errorCode = "VE-000";
        try {
            Account check = accountRepository.findAccountByAccountNo(accountNo).orElse(null);
            if(check == null) {
                errorCode = "VE-102";
                return new ResultEntity<List<AccountLogDTO>>(null, errorCode);
            }
            Timestamp startDateTimeStamp = Timestamp.valueOf(startDate + " 00:00:00");
            Timestamp endDateTimeStamp = Timestamp.valueOf(endDate + " 23:59:59");
            logs = accountLogRepository.findAllAccountLogByAccountNoAndDate(accountNo, startDateTimeStamp, endDateTimeStamp);
            List<AccountLogDTO> dtos = new ArrayList<>();
            if(logs.isEmpty()) {
                errorCode = "VE-201";
            } else {
                for (AccountLog log : logs) {
                    AccountLogDTO dto = new AccountLogDTO();
                    dto.setId(log.getId());
                    dto.setAccountNo(log.getAccountNo());
                    dto.setTranDate(log.getTranDate());
                    dto.setDescription(log.getDescription());
                    dto.setTranType(log.getTranType());
                    dto.setAmount(FormatUtil.doubleFormatToString(log.getAmount()));
                    dtos.add(dto);
                }
                errorCode = "VE-000";
            }
            return new ResultEntity<>(dtos, errorCode);
        } catch (Exception e) {
            if(e.getMessage() == null || e.getMessage().isEmpty()) {
                throw new Exception("VE-999");
            } else {
                throw new Exception(e.getMessage());
            }
        }
    }
}
