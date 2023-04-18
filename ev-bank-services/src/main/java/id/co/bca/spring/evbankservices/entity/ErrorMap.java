package id.co.bca.spring.evbankservices.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "error_map")
public class ErrorMap {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "description")
    private String description;
}
