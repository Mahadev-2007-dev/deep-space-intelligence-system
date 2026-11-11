package org.deepspace.dsis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@SpringBootApplication
@EnableScheduling
public class DsisApplication {

    private static final Logger log = LoggerFactory.getLogger(DsisApplication.class);

    public static void main(String[] args) {
        // Ensure data directory exists for SQLite
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        // Initialize OpenCV
        try {
            nu.pattern.OpenCV.loadLocally();
            log.info(">>> OpenCV native library loaded successfully for DSIS Space Object Scanner.");
        } catch (Throwable t) {
            log.warn(">>> OpenCV native load warning: {}. Space Object Scanner will use high-performance Java CV fallback if needed.", t.getMessage());
        }

        SpringApplication.run(DsisApplication.class, args);
        log.info("====================================================================");
        log.info(">>> DEEP SPACE INTELLIGENCE SYSTEM (DSIS) BACKEND READY ON PORT 8080 <<<");
        log.info(">>> Tagline: INTELLIGENCE BEYOND EARTH <<<");
        log.info("====================================================================");
    }
}
