package id.co.bca.spring.evbankservices.controller;

import id.co.bca.spring.evbankservices.entity.Account;
import id.co.bca.spring.evbankservices.entity.AccountLog;
import id.co.bca.spring.evbankservices.entity.response.BaseResponse;
import id.co.bca.spring.evbankservices.entity.response.ResultEntity;
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
    public ResponseEntity<ResultEntity<AccountDTO>> getAccountByAccountNo (@PathVariable("account_no") String accountNo) {
        ResultEntity<AccountDTO> result;

        try {
            result = accountService.getAccountByAccountNo(accountNo);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            String[] arr = e.getMessage().split(";");
            HashMap<String, String> map = new HashMap<>();
            for(int i =1; i<arr.length;i++) {
                map.put(String.valueOf(i), arr[i]);
            }

            AccountDTO outputSchema = new AccountDTO();
            return new ResponseEntity<>(new ResultEntity<AccountDTO>(outputSchema, arr[0], map), HttpStatus.OK);
        }
    }

    @PostMapping("")
    public ResponseEntity<ResultEntity<AccountDTO>> saveAccount(@RequestBody AccountDTO accountDTO) {
        ResultEntity<AccountDTO> resultEntity;

        try {
            Account account = Account.fromDTO(accountDTO);
            resultEntity = accountService.saveAccount(account);
            return new ResponseEntity<>(resultEntity, HttpStatus.OK);
        } catch (Exception e) {
            String[] arr = e.getMessage().split(";");
            HashMap<String, String> map = new HashMap<>();
            for(int i =1; i<arr.length;i++) {
                map.put(String.valueOf(i), arr[i]);
            }

            AccountDTO outputSchema = new AccountDTO();
            return new ResponseEntity<>(new ResultEntity<AccountDTO>(outputSchema, arr[0], map), HttpStatus.OK);
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<ResultEntity<HashMap<String, String>>> checkAccountBalance(@RequestBody AccountDTO accountDTO) {
        ResultEntity<HashMap<String, String>> resultEntity;
        try {
            Account account = Account.fromDTO(accountDTO);
            resultEntity = accountService.getAccountByCardNoAndPin(account);
            System.out.println(resultEntity.getData().get("balance"));
            return new ResponseEntity<>(resultEntity, HttpStatus.OK);
        } catch (Exception e) {
            String[] arr = e.getMessage().split(";");
            HashMap<String, String> map = new HashMap<>();
            for(int i =1; i<arr.length;i++) {
                map.put(String.valueOf(i), arr[i]);
            }

            HashMap<String, String> outputSchema = new HashMap<>();
            return new ResponseEntity<>(new ResultEntity<>(outputSchema, arr[0], map), HttpStatus.OK);
        }
    }

    @PutMapping("/debit-balance")
    public ResponseEntity<ResultEntity<HashMap<String, String>>> debitBalanceAccount(@RequestBody HashMap<String, String> body) {
        if (body.isEmpty() || !body.containsKey("debit_account_no") || !body.containsKey("debit_balance")) {
            HashMap<String, String> responseMap = new HashMap<>();
            responseMap.put("status", "Bad request");
            return new ResponseEntity<>(new ResultEntity<>(responseMap, "VE-999"), HttpStatus.BAD_REQUEST);
        }
        String accountNo = body.get("debit_account_no");
        double debitValue = Double.parseDouble(body.get("debit_balance"));
        String description = body.get("description");
        ResultEntity<HashMap<String,String>> resultEntity;

        try {
            resultEntity = accountService.debitBalanceByAccountNo(accountNo, debitValue, description);
            return new ResponseEntity<>(resultEntity, HttpStatus.OK);
        } catch (Exception e) {
            String[] arr = e.getMessage().split(";");
            HashMap<String, String> map = new HashMap<>();
            for(int i =1; i<arr.length;i++) {
                map.put(String.valueOf(i), arr[i]);
            }

            HashMap<String, String> outputSchema = new HashMap<>();
            return new ResponseEntity<>(new ResultEntity<>(outputSchema, arr[0], map), HttpStatus.OK);
        }
    }

    @PutMapping("/credit-balance")
    public ResponseEntity<ResultEntity<HashMap<String, String>>> creditBalanceByAccountNo(@RequestBody HashMap<String, String> body) {
        if (body.isEmpty() || !body.containsKey("credit_account_no") || !body.containsKey("credit_balance")) {
            HashMap<String, String> responseMap = new HashMap<>();
            responseMap.put("status", "Bad request");
            return new ResponseEntity<>(new ResultEntity<>(responseMap, "VE-999"), HttpStatus.BAD_REQUEST);
        }
        String accountNo = body.get("credit_account_no");
        double creditValue = Double.parseDouble(body.get("credit_balance"));
        String description = body.get("description");
        ResultEntity<HashMap<String,String>> resultEntity;

        try {
            resultEntity = accountService.creditBalanceToAccountNo(accountNo, creditValue, description);
            return new ResponseEntity<>(resultEntity, HttpStatus.OK);
        } catch (Exception e) {
            String[] arr = e.getMessage().split(";");
            HashMap<String, String> map = new HashMap<>();
            for(int i =1; i<arr.length;i++) {
                map.put(String.valueOf(i), arr[i]);
            }

            HashMap<String, String> outputSchema = new HashMap<>();
            return new ResponseEntity<>(new ResultEntity<>(outputSchema, arr[0], map), HttpStatus.OK);
        }
    }

    @GetMapping("/account-statement-by-date")
    public ResponseEntity<ResultEntity<List<AccountLogDTO>>> getAccountStatementByDate(@RequestBody HashMap<String, String> body) {
        ResultEntity<List<AccountLogDTO>> resultEntity;
        if (body.isEmpty() || !body.containsKey("start_date") || !body.containsKey("end_date") || !body.containsKey("account_no")) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        try {
            String accountNo = body.get("account_no");
            String startDate = body.get("start_date");
            String endDate = body.get("end_date");

            resultEntity = accountService.getAccountStatementByDate(accountNo,startDate,endDate);
            return new ResponseEntity<>(resultEntity,HttpStatus.OK);
        } catch (Exception e) {
            String[] arr = e.getMessage().split(";");
            HashMap<String, String> map = new HashMap<>();
            for(int i =1; i<arr.length;i++) {
                map.put(String.valueOf(i), arr[i]);
            }

            List<AccountLogDTO> dtos = new ArrayList<>();
            return new ResponseEntity<>(new ResultEntity<>(dtos, arr[0], map), HttpStatus.OK);
        }
    }
}
