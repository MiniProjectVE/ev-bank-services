package id.co.bca.spring.evbankservices.config;

import id.co.bca.spring.evbankservices.service.AccountDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    @Bean
    public AccountDetailService accountDetailService() {
        return new AccountDetailService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        security.csrf().disable()
                .httpBasic()
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.PUT, "/api/account/credit-balance", "/api/account/debit-balance").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/account/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .formLogin().permitAll()
                .and().logout().permitAll();
        return security.build();
    }
}
