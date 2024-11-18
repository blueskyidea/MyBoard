package me.haneul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing  //createdDate, modifiedDate 관리
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class MyBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyBoardApplication.class, args);
    }

}