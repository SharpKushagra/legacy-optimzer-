package com.tcsoptimizer.legacy_optimizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.tcsoptimizer.legacy_optimizer",       // controllers and local packages
        "com.example.codeanalyzer.service"         // your service classes
})
public class LegacyOptimizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LegacyOptimizerApplication.class, args);
    }

}
