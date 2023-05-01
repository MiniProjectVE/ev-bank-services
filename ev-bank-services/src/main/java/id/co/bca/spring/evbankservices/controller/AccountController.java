package id.co.bca.spring.evbankservices.controller;

import id.co.bca.spring.evbankservices.entity.Account;
import id.co.bca.spring.evbankservices.entity.AccountLog;
import id.co.bca.spring.evbankservices.entity.response.BaseResponse;
import id.co.bca.spring.evbankservices.model.AccountDTO;
import id.co.bca.spring.evbankservices.model.AccountLogDTO;
import id.co.bca.spring.evbankservices.service.AccountService;
import id.co.bca.spring.evbankservices.util.FormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/{account_no}")
    public ResponseEntity<BaseResponse<AccountDTO>> getAccountById(@PathVariable("account_no") String accountNo) {
        BaseResponse<AccountDTO> response = new BaseResponse<>();
        Account account = accountService.getAccountByAccountNo(accountNo);

        if(account == null) {
            response.setErrorCode("992");
            response.setErrorMessage("Account not found. Account No: " + accountNo);
            response.setPayload(null);
        } else {
            AccountDTO dto = new AccountDTO();
            dto.setAccountNo(account.getAccountNo());
            dto.setAccountName(account.getAccountName());
            dto.setCardNo(account.getCardNo());
            dto.setBalance(FormatUtil.doubleFormatToString(account.getBalance()));
            dto.setPin("!CONFIDENTIAL!");

            response.setPayload(dto);
            response.setErrorCode("000");
            response.setErrorMessage("Success.");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<BaseResponse<AccountDTO>> saveAccount(@RequestBody AccountDTO accountDTO) {
        BaseResponse<AccountDTO> response = new BaseResponse<>();
        Account account = Account.fromDTO(accountDTO);
        Account insertedAccount = accountService.saveAccount(account);
        if(insertedAccount == null) {
            response.setPayload(null);
            response.setErrorCode("999");
            response.setErrorMessage("Failed.");
        } else {
            response.setPayload(insertedAccount.toDTO());
            response.setErrorCode("000");
            response.setErrorMessage("Success.");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/balance")
    public ResponseEntity<BaseResponse<HashMap<String, String>>> checkAccountBalance(@RequestBody AccountDTO accountDTO) {
        HashMap<String, String> output = new HashMap<>();
        BaseResponse<HashMap<String, String>> response = new BaseResponse<>();

        Account account = Account.fromDTO(accountDTO);

        Account result = accountService.getAccountByCardNoAndPin(account);
        if(result == null) {
            response.setErrorMessage("Account not found. ID: "+ account.getId());
            response.setErrorCode("992");
            response.setPayload(null);
        } else {
            output.put("account_no", result.getAccountNo());
            output.put("balance", FormatUtil.doubleFormatToString(result.getBalance()));
            response.setPayload(output);
            response.setErrorCode("000");
            response.setErrorMessage("Success");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/debit-balance")
    public ResponseEntity<BaseResponse<String>> debitBalanceAccount(@RequestBody HashMap<String, String> body) {
        BaseResponse<String> response = new BaseResponse<>();
        if (body.isEmpty() || !body.containsKey("debit_account_no") || !body.containsKey("debit_balance")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        String accountNo = body.get("debit_account_no");
        double debitValue = Double.parseDouble(body.get("debit_balance"));
        String description = body.get("description");

        int result = accountService.debitBalanceByAccountNo(accountNo, debitValue, description);
        response.setPayload(null);
        switch (result) {
            case 0 -> {
                response.setErrorMessage("Account not found. Account No. " + accountNo);
                response.setErrorCode("992");
            }
            case 1 -> {
                response.setErrorMessage("Account balance has been debited");
                response.setErrorCode("000");
            }
            case 2 -> {
                response.setErrorMessage("Insufficient balance for account " + accountNo);
                response.setErrorCode("301");
            }
            default -> {
                response.setErrorMessage("Failed posting transaction(s)");
                response.setErrorCode("998");
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/credit-balance")
    public ResponseEntity<BaseResponse<String>> creditBalanceByAccountNo(@RequestBody HashMap<String, String> body) {
        BaseResponse<String> response = new BaseResponse<>();
        if (body.isEmpty() || !body.containsKey("credit_account_no") || !body.containsKey("credit_balance")) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String accountNo = body.get("credit_account_no");
        double creditValue = Double.parseDouble(body.get("credit_balance"));
        String description = body.get("description");

        int result = accountService.creditBalanceToAccountNo(accountNo, creditValue, description);
        response.setPayload(null);

        switch (result) {
            case 0 -> {
                response.setErrorMessage("Account not found. Account No: " + accountNo);
                response.setErrorCode("992");
            }
            case 1 -> {
                response.setErrorMessage("Account balance has been credited");
                response.setErrorCode("000");
            }
            default -> {
                response.setErrorMessage("Failed posting transaction(s)");
                response.setErrorCode("998");
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/account-statement-by-date")
    public ResponseEntity<BaseResponse<List<AccountLogDTO>>> getAccountStatementByDate(@RequestBody HashMap<String, String> body) {
        BaseResponse<List<AccountLogDTO>> response = new BaseResponse<>();
        if (body.isEmpty() || !body.containsKey("start_date") || !body.containsKey("end_date") || !body.containsKey("account_no")) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String accountNo = body.get("account_no");
        String startDate = body.get("start_date");
        String endDate = body.get("end_date");

        List<AccountLog> accountLogs = accountService.getAccountStatementByDate(accountNo, startDate, endDate);

        if(accountLogs == null || accountLogs.isEmpty()) {
            response.setErrorMessage("No transaction found.");
            response.setErrorCode("401");
            response.setPayload(null);
        } else {
            List<AccountLogDTO> dtos = new ArrayList<>();
            for (AccountLog log : accountLogs) {
                AccountLogDTO dto = new AccountLogDTO();
                dto.setTranDate(log.getTranDate());
                dto.setTranType(log.getTranType());
                dto.setAmount(FormatUtil.doubleFormatToString(log.getAmount()));
                dto.setDescription(log.getDescription());
                dto.setAccountNo(log.getAccountNo());
                dtos.add(dto);
            }
            response.setErrorCode("000");
            response.setErrorMessage("Success.");
            response.setPayload(dtos);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
