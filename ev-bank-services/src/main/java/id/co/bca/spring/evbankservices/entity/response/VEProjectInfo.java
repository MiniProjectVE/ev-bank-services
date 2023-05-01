package id.co.bca.spring.evbankservices.entity.response;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Component
public class VEProjectInfo {
    @Getter
    private static Environment environment = null;

    @Autowired
    public VEProjectInfo(Environment props) {
        environment = props;
    }
}
