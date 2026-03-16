package com.outfy.outfy_backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Objects;

@SpringBootApplication
@EnableScheduling
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
			System.setProperty("JWT_SECRET", Objects.toString(dotenv.get("JWT_SECRET")));
			System.setProperty("ACCESS_TOKEN_EXPIRATION", Objects.toString(dotenv.get("ACCESS_TOKEN_EXPIRATION")));
			System.setProperty("REFRESH_TOKEN_EXPIRATION", Objects.toString(dotenv.get("REFRESH_TOKEN_EXPIRATION")));
			System.setProperty("MAIL_USERNAME", Objects.toString(dotenv.get("MAIL_USERNAME")));
			System.setProperty("MAIL_PASSWORD", Objects.toString(dotenv.get("MAIL_PASSWORD")));
			System.setProperty("MAIL_FROM", Objects.toString(dotenv.get("MAIL_FROM")));
			System.setProperty("OTP_EXPIRATION_MINUTES", Objects.toString(dotenv.get("OTP_EXPIRATION_MINUTES")));
			System.setProperty("GOOGLE_CLIENT_ID", Objects.toString(dotenv.get("GOOGLE_CLIENT_ID")));
		}

		SpringApplication.run(OutfyBackendApplication.class, args);
	}

	private static void setSystemPropertyIfAbsent(String key, String defaultValue, Dotenv dotenv) {
		String value = dotenv != null ? dotenv.get(key) : null;
		if (value == null || value.equalsIgnoreCase("null") || value.isBlank()) {
			value = defaultValue;
		}
		System.setProperty(key, value);
	}

}