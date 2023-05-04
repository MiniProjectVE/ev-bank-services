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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
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

    @Value("${get-crypto-transaction-url}")
    private String getCryptoTransactionUrl;

    @Value("${kafka-topic-name}")
    private String kafkaTopicName;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Transactional(rollbackOn = Exception.class)
    public ResultEntity<PaymentDTO> insertPayment(PaymentDTO paymentDTO) throws Exception {
        if(paymentDTO == null) {
            return new ResultEntity<>(null, "VE-022");
        }

        if (paymentDTO.getCryptoTranId().trim().isEmpty() || paymentDTO.getAccountTo().trim().isEmpty()) {
            return new ResultEntity<>(null, "VE-022");
        }

        ResponseEntity<String> cryptoTrx = restTemplate.exchange(getCryptoTransactionUrl+paymentDTO.getCryptoTranId(), HttpMethod.GET, null, String.class);

        JsonNode cryptoResRoot = objectMapper.readTree(cryptoTrx.getBody());
        JsonNode cryptoTrxErrorSchema = cryptoResRoot.path("error_schema");
        if (!cryptoTrxErrorSchema.get("error_code").textValue().equalsIgnoreCase("VE-000")) {
            return new ResultEntity<PaymentDTO>(null, "VE-022");
        }
        JsonNode cryptoTrxOutputSchema = cryptoResRoot.path("output_schema");

        String resultErrorCode = "VE-000";
        try {
            String debitAccountNo = cryptoTrxOutputSchema.get("accountNo").textValue();
            double accountedBalance = cryptoTrxOutputSchema.get("totalAmount").doubleValue();
            String dbCrBalance = String.valueOf(accountedBalance);

            HashMap<String, String> dbReqBody = new HashMap<>();
            dbReqBody.put("debit_account_no", debitAccountNo);
            dbReqBody.put("debit_balance", dbCrBalance);

            HttpEntity<HashMap<String, String>> dbHttpBody = new HttpEntity<>(dbReqBody);

            ResponseEntity<String> debitResponse = restTemplate.exchange(debitServiceUrl, HttpMethod.PUT, dbHttpBody, String.class);

            JsonNode dbResRoot = objectMapper.readTree(debitResponse.getBody());
            JsonNode dbErrorSchema = dbResRoot.path("error_schema");
            String dbErrorCode = dbErrorSchema.get("error_code").textValue();

            if (dbErrorCode.equalsIgnoreCase("VE-000")) {
               if(!paymentDTO.getAccountTo().trim().isEmpty()) {
                   HashMap<String, String> crReqBody = new HashMap<>();
                   crReqBody.put("credit_account_no", paymentDTO.getAccountTo());
                   crReqBody.put("credit_balance", dbCrBalance);

                   HttpEntity<HashMap<String, String>> crHttpBody = new HttpEntity<>(crReqBody);

                   ResponseEntity<String> creditResponse = restTemplate.exchange(creditServiceUrl, HttpMethod.PUT, crHttpBody, String.class);
                   JsonNode crResRoot = objectMapper.readTree(creditResponse.getBody());
                   JsonNode crErrorSchema = crResRoot.path("error_schema");
                   String crErrorCode = crErrorSchema.get("error_code").textValue();
                   if(!crErrorCode.equalsIgnoreCase("VE-000")) {
                       crReqBody.replace("credit_account_no", debitAccountNo);
                       crReqBody.put("description", "Reversal Transaction");
                       HttpEntity<HashMap<String, String>> reverseDb = new HttpEntity<>(crReqBody);
                       System.out.println(crReqBody);
                       restTemplate.exchange(creditServiceUrl, HttpMethod.PUT, reverseDb, String.class);

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
                payment.setAccountFrom(debitAccountNo);
                payment.setAccountTo(paymentDTO.getAccountTo());
                payment.setAmount(Double.parseDouble(dbCrBalance));
                payment.setDescription(paymentDTO.getDescription());
                payment.setRefNo(newRefNo);
                payment.setTranDate(date);

                Payment result = paymentRepository.save(payment);
                String kafkaMessage = "{\"trx_id\":\""+payment.getCryptoTranId()+"\",\"status\":\"1\"}";
                kafkaTemplate.send(kafkaTopicName, kafkaMessage);

            }

            return new ResultEntity<>(paymentDTO, resultErrorCode);
        } catch (Exception e) {
            e.printStackTrace();
            if(e.getMessage() == null || e.getMessage().isEmpty()) {
                throw new Exception("VE-999");
            } else {
                throw new Exception(e.getMessage());
            }
        }
    }
}
