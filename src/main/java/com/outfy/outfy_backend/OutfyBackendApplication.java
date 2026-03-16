package com.outfy.outfy_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import io.github.cdimascio.dotenv.Dotenv;

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

		// Set default values for properties
		setSystemPropertyIfAbsent("DB_HOST", "localhost", dotenv);
		setSystemPropertyIfAbsent("DB_PORT", "5432", dotenv);
		setSystemPropertyIfAbsent("DB_NAME", "outfy_db", dotenv);
		setSystemPropertyIfAbsent("DB_USERNAME", "outfy", dotenv);
		setSystemPropertyIfAbsent("DB_PASSWORD", "outfy244466666", dotenv);
		setSystemPropertyIfAbsent("JWT_SECRET", "outfy_jwt_secret_key_change_in_production_12345678", dotenv);
		setSystemPropertyIfAbsent("ACCESS_TOKEN_EXPIRATION", "1800000", dotenv);
		setSystemPropertyIfAbsent("REFRESH_TOKEN_EXPIRATION", "604800000", dotenv);
		setSystemPropertyIfAbsent("OTP_EXPIRATION_MINUTES", "5", dotenv);
		setSystemPropertyIfAbsent("MAIL_FROM", "noreply@outfy.com", dotenv);

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