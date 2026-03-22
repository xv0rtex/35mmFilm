package com.filmpin.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class VisitorFileLogger {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ReentrantLock lock = new ReentrantLock();

    @Value("${app.visitorlog.file:logs/filmpin-visitors.log}")
    private String logFile;

    public void append(HttpServletRequest request, String ipAddress, String userAgent) {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        if (query != null && !query.isBlank()) {
            path = path + "?" + query;
        }

        String timestamp = LocalDateTime.now().format(TS);
        String ua = userAgent == null ? "" : userAgent;
        String ip = ipAddress == null ? "" : ipAddress;
        String line = "[" + timestamp + "] IP: " + ip + " | UA: " + ua + " | Path: " + path + System.lineSeparator();

        lock.lock();
        try {
            Path filePath = Path.of(logFile);
            Path parent = filePath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            Files.writeString(
                    filePath,
                    line,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND
            );
        } catch (Exception ignored) {
        } finally {
            lock.unlock();
        }
    }
}

