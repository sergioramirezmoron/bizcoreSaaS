package com.bizcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Entry point de BizCore.
 *
 * @EnableTransactionManagement(order = 5) garantiza que el TransactionInterceptor
 * se registre con precedencia 5 (outer), dejando al TenantAspect (@Order(10))
 * ejecutarse DENTRO de la transacción activa.
 */
@SpringBootApplication(scanBasePackages = "com.bizcore")
@EnableTransactionManagement(order = 5)
public class BizCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BizCoreApplication.class, args);
    }
}
