package com.boot.ktn.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    @Getter
    public static class FileConfig {
        private final long maxFileSize;
        private final int maxResultSize;
        private final int maxFilesPerUpload;

        public FileConfig(long maxFileSize, int maxResultSize, int maxFilesPerUpload) {
            this.maxFileSize = maxFileSize;
            this.maxResultSize = maxResultSize;
            this.maxFilesPerUpload = maxFilesPerUpload;
        }
    }

    @Bean
    public FileConfig fileConfig() {
        long maxFileSize = Long.parseLong(dotenv.get("MAX_FILE_SIZE", "314572800")); // Default to 300MB
        int maxResultSize = Integer.parseInt(dotenv.get("MAX_RESULT_SIZE", "300")); // Default to 300 records
        int maxFilesPerUpload = Integer.parseInt(dotenv.get("MAX_FILES_PER_UPLOAD", "10")); // Default to 10 files
        return new FileConfig(maxFileSize, maxResultSize, maxFilesPerUpload);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}