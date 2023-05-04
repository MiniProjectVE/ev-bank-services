package id.co.bca.spring.evbankservices.controller;

import id.co.bca.spring.evbankservices.entity.response.ResultEntity;
import id.co.bca.spring.evbankservices.model.PaymentDTO;
import id.co.bca.spring.evbankservices.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("")
    public ResponseEntity<ResultEntity<PaymentDTO>> insertPayment(@RequestBody PaymentDTO paymentDTO) {
        ResultEntity<PaymentDTO> resultEntity;
        try {
            resultEntity = paymentService.insertPayment(paymentDTO);
            return new ResponseEntity<>(resultEntity, HttpStatus.OK);
        } catch (Exception e) {
            String[] arr = e.getMessage().split(";");
            HashMap<String, String> map = new HashMap<>();
            for(int i =1; i<arr.length;i++) {
                map.put(String.valueOf(i), arr[i]);
            }

            PaymentDTO errorDTO = new PaymentDTO();
            return new ResponseEntity<>(new ResultEntity<>(errorDTO, arr[0], map), HttpStatus.OK);
        }
    }
}
