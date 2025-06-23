package com.boot.ktn;

import io.github.cdimascio.dotenv.Dotenv;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@MapperScan("com.boot.ktn.mapper.**")
@ComponentScan(basePackages = {"com.boot.ktn.config", "com.boot.ktn.security", "com.boot.ktn", "com.boot.ktn.controller"}) // 패키지 스캔 추가
public class KtnApplication {
	private static final Logger logger = LoggerFactory.getLogger(com.boot.ktn.KtnApplication.class);

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(com.boot.ktn.KtnApplication.class);

		String errorMessage;

		// Set default profile to 'dev' if not specified
		Map<String, Object> defaultProperties = new HashMap<>();
		defaultProperties.put("spring.profiles.active", "dev");
		application.setDefaultProperties(defaultProperties);

		// Load .env if present
		try {
			Dotenv dotenv = Dotenv.configure()
					.directory("./")
					.filename(".env")
					.ignoreIfMissing()
					.load();
			System.out.println("Loaded .env");
			System.out.println("SPRING_DATASOURCE_URL = " + dotenv.get("SPRING_DATASOURCE_URL"));
			System.out.println("SPRING_DATASOURCE_USERNAME = " + dotenv.get("SPRING_DATASOURCE_USERNAME"));
			System.out.println("SPRING_DATASOURCE_PASSWORD = " + (dotenv.get("SPRING_DATASOURCE_PASSWORD") != null ? "[REDACTED]" : "null"));
			System.out.println("CORS_ALLOWED_ORIGINS = " + dotenv.get("CORS_ALLOWED_ORIGINS"));
			System.out.println("SPRING_PROFILES_ACTIVE = " + dotenv.get("SPRING_PROFILES_ACTIVE"));
			System.out.println("API_BASE_PATH = " + dotenv.get("API_BASE_PATH")); // 추가: api.base.path 확인

			// Add .env to system properties
			dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

			// Validate critical variables
			if (dotenv.get("SPRING_DATASOURCE_USERNAME") == null || dotenv.get("SPRING_DATASOURCE_PASSWORD") == null) {
				errorMessage = "ERROR: SPRING_DATASOURCE_USERNAME and SPRING_DATASOURCE_PASSWORD must be set in .env";
				logger.error(errorMessage);
				System.err.println(errorMessage);
			}
			if (dotenv.get("API_BASE_PATH") == null) { // 추가: api.base.path 검증
				System.setProperty("api.base.path", "api"); // 기본값 설정
				logger.warn("API_BASE_PATH not set in .env, defaulting to 'api'");
			}
			System.out.println("API_BASE_PATH = " + dotenv.get("API_BASE_PATH"));
			if (dotenv.get("API_BASE_PATH") == null) {
				System.setProperty("api.base.path", "api");
				logger.warn("API_BASE_PATH not set in .env, defaulting to 'api'");
			} else {
				// API_BASE_PATH 정규화: 슬래시 제거 및 유효성 검사
				String apiBasePath = dotenv.get("API_BASE_PATH").trim().replaceAll("^/+|/+$", "");
				if (apiBasePath.isEmpty()) {
					apiBasePath = "api";
					logger.warn("API_BASE_PATH is empty in .env, defaulting to 'api'");
				}
				System.setProperty("api.base.path", apiBasePath);
				System.out.println("Normalized API_BASE_PATH = " + apiBasePath);
			}
			// ...
			if (System.getenv("API_BASE_PATH") == null) {
				System.setProperty("api.base.path", "api");
				logger.warn("API_BASE_PATH not set in environment, defaulting to 'api'");
			} else {
				// 환경 변수에서도 정규화
				String apiBasePath = System.getenv("API_BASE_PATH").trim().replaceAll("^/+|/+$", "");
				if (apiBasePath.isEmpty()) {
					apiBasePath = "api";
					logger.warn("API_BASE_PATH is empty in environment, defaulting to 'api'");
				}
				System.setProperty("api.base.path", apiBasePath);
				System.out.println("Normalized API_BASE_PATH from env = " + apiBasePath);
			}
		} catch (Exception e) {
			errorMessage = "No .env file found or failed to load: ";
			logger.error(errorMessage, e.getMessage(), e);
			System.out.println(errorMessage + e.getMessage());
			// Check system environment variables
			if (System.getenv("SPRING_DATASOURCE_USERNAME") == null || System.getenv("SPRING_DATASOURCE_PASSWORD") == null) {
				errorMessage = "ERROR: SPRING_DATASOURCE_USERNAME and SPRING_DATASOURCE_PASSWORD must be set";
				logger.error(errorMessage);
				System.out.println(errorMessage);
			}
			// Set default api.base.path if not set
			if (System.getenv("API_BASE_PATH") == null) { // 추가
				System.setProperty("api.base.path", "api");
				logger.warn("API_BASE_PATH not set in environment, defaulting to 'api'");
			}
		}

		application.run(args);
	}
}