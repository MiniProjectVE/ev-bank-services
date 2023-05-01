package id.co.bca.spring.evbankservices.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.bca.spring.evbankservices.entity.Payment;
import id.co.bca.spring.evbankservices.entity.response.ResultEntity;
import id.co.bca.spring.evbankservices.model.PaymentDTO;
import id.co.bca.spring.evbankservices.repository.PaymentRepository;
import id.co.bca.spring.evbankservices.util.DateUtil;
import id.co.bca.spring.evbankservices.util.FormatUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.util.HashMap;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${debit-service-url}")
    private String debitServiceUrl;

    @Value("${credit-service-url}")
    private String creditServiceUrl;

    @Transactional(rollbackOn = Exception.class)
    public ResultEntity<PaymentDTO> insertPayment(PaymentDTO paymentDTO) throws Exception {
        if(paymentDTO == null) {
            return new ResultEntity<>(null, "VE-022");
        }

        if (paymentDTO.getAccountFrom().trim().isEmpty() || paymentDTO.getCryptoTranId().trim().isEmpty() || paymentDTO.getAccountTo().trim().isEmpty()) {
            return new ResultEntity<>(null, "VE-022");
        }

//      #TODO: Check Crypto Transaction
        boolean isTransactionExists = true;

        if (isTransactionExists) {
            String resultErrorCode = "VE-000";
            try {
                HashMap<String, String> dbReqBody = new HashMap<>();
                dbReqBody.put("debit_account_no", paymentDTO.getAccountFrom());
                dbReqBody.put("debit_balance", paymentDTO.getAmount());

                HttpEntity<HashMap<String, String>> dbHttpBody = new HttpEntity<>(dbReqBody);

                ResponseEntity<String> debitResponse = restTemplate.exchange(debitServiceUrl, HttpMethod.PUT, dbHttpBody, String.class);

                JsonNode dbResRoot = objectMapper.readTree(debitResponse.getBody());
                JsonNode dbErrorSchema = dbResRoot.path("error_schema");
                String dbErrorCode = dbErrorSchema.get("error_code").textValue();

                if (dbErrorCode.equalsIgnoreCase("VE-000")) {
                   if(!paymentDTO.getAccountTo().trim().isEmpty()) {
                       HashMap<String, String> crReqBody = new HashMap<>();
                       crReqBody.put("credit_account_no", paymentDTO.getAccountTo());
                       crReqBody.put("credit_balance", paymentDTO.getAmount());

                       HttpEntity<HashMap<String, String>> crHttpBody = new HttpEntity<>(crReqBody);

                       ResponseEntity<String> creditResponse = restTemplate.exchange(creditServiceUrl, HttpMethod.PUT, crHttpBody, String.class);
                       JsonNode crResRoot = objectMapper.readTree(creditResponse.getBody());
                       JsonNode crErrorSchema = crResRoot.path("error_schema");
                       String crErrorCode = crErrorSchema.get("error_code").textValue();
                       if(!crErrorCode.equalsIgnoreCase("VE-000")) {
                           resultErrorCode = crErrorCode;
                       }
                   }
                } else {
                    resultErrorCode = dbErrorCode;
                }

                if(resultErrorCode.equalsIgnoreCase("VE-000")) {
                    Date date = DateUtil.getTodayDate();
                    Payment lastPayment = paymentRepository.findTopByOrderByTranDateToday(date);
                    String newRefNo = "";

                    if(lastPayment != null) {
                        String lastRefNo = lastPayment.getRefNo();
                        String prefix = lastRefNo.substring(0, 11);
                        int seq = Integer.parseInt(lastRefNo.substring(11));
                        seq += 1;
                        String temp = "0000" + seq;
                        String suffix = temp.substring(temp.length() - 4);
                        newRefNo = prefix + suffix;
                    } else {
                        String prefix = "RN-" + FormatUtil.dateToStringFormatNoSpinal(date);
                        String suffix = "0001";
                        newRefNo = prefix + suffix;
                    }

                    Payment payment = new Payment();
                    payment.setCryptoTranId(Integer.parseInt(paymentDTO.getCryptoTranId()));
                    payment.setAccountFrom(paymentDTO.getAccountFrom());
                    payment.setAccountTo(paymentDTO.getAccountTo());
                    payment.setAmount(Double.parseDouble(paymentDTO.getAmount()));
                    payment.setDescription(paymentDTO.getDescription());
                    payment.setRefNo(newRefNo);
                    payment.setTranDate(date);

                    paymentRepository.save(payment);
                }

                return new ResultEntity<>(paymentDTO, resultErrorCode);
            } catch (Exception e) {
                if(e.getMessage() == null || e.getMessage().isEmpty()) {
                    throw new Exception("VE-999");
                } else {
                    throw new Exception(e.getMessage());
                }
            }
        }
        return new ResultEntity<PaymentDTO>(null, "VE-102");
    }
}
