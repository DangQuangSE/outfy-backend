package com.outfy.outfy_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Objects;

@SpringBootApplication
@PropertySources({
    @PropertySource("classpath:application.properties"),
    @PropertySource(value = "file:.env", ignoreResourceNotFound = true)
})
public class OutfyBackendApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
        if (dotenv != null) {
            System.setProperty("DB_HOST", Objects.toString(dotenv.get("DB_HOST")));
            System.setProperty("DB_PORT", Objects.toString(dotenv.get("DB_PORT")));
            System.setProperty("DB_NAME", Objects.toString(dotenv.get("DB_NAME")));
            System.setProperty("DB_USERNAME", Objects.toString(dotenv.get("DB_USERNAME")));
            System.setProperty("DB_PASSWORD", Objects.toString(dotenv.get("DB_PASSWORD")));
        }

        SpringApplication.run(OutfyBackendApplication.class, args);
    }

}
