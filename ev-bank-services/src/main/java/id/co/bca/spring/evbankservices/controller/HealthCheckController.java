package id.co.bca.spring.evbankservices.controller;


import id.co.bca.spring.evbankservices.entity.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckController {
    @GetMapping("")
    public ResponseEntity<BaseResponse<String>> healthCheck() {
        BaseResponse<String> response = new BaseResponse<>();
        response.setPayload("Health OK!");
        response.setErrorCode("000");
        response.setErrorMessage("Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
