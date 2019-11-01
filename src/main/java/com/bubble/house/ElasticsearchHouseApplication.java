package com.bubble.house;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ElasticsearchHouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchHouseApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello bubble...";
    }

}
