package id.co.bca.spring.evbankservices.controller;

import id.co.bca.spring.evbankservices.entity.Account;
import id.co.bca.spring.evbankservices.entity.response.BaseResponse;
import id.co.bca.spring.evbankservices.model.AccountDTO;
import id.co.bca.spring.evbankservices.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/{account_no}")
    public ResponseEntity<BaseResponse<Account>> getAccountById(@PathVariable("account_no") String accountNo) {
        BaseResponse<Account> response = new BaseResponse<>();
        Account account = accountService.getAccountByAccountNo(accountNo);
        response.setPayload(account);
        if(account == null) {
            response.setErrorCode("992");
            response.setErrorMessage("Account not found. Account No: " + accountNo);
        } else {
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
            output.put("balance", String.valueOf(result.getBalance()));
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
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        String accountNo = body.get("debit_account_no");
        double debitValue = Double.parseDouble(body.get("debit_balance"));

        int result = accountService.debitBalanceByAccountNo(accountNo, debitValue);
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
            default -> {
                response.setErrorMessage("Insufficient balance for account " + accountNo);
                response.setErrorCode("301");
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

        int result = accountService.creditBalanceToAccountNo(accountNo, creditValue);
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
                response.setErrorCode("302");
                response.setErrorMessage("Failed to credit balance to account.");
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
