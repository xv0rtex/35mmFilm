package com.filmpin.config;

import com.filmpin.entity.VisitorLog;
import com.filmpin.service.VisitorLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class VisitorLogInterceptor implements HandlerInterceptor {

    @Autowired
    private VisitorLogService visitorLogService;
    
    @Autowired
    private VisitorFileLogger visitorFileLogger;
    
    @Value("${app.visitorlog.db.enabled:true}")
    private boolean dbLoggingEnabled;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        
        String uri = request.getRequestURI();
        if (uri.startsWith("/css/") ||
                uri.startsWith("/js/") ||
                uri.startsWith("/images/") ||
                uri.equals("/favicon.png") ||
                uri.equals("/favicon.png") ||
                uri.equals("/manifest.webmanifest") ||
                uri.equals("/service-worker.js")) {
            return true;
        }

        try {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }

            String userAgent = request.getHeader("User-Agent");
            visitorFileLogger.append(request, ip, userAgent);

            if (!dbLoggingEnabled) {
                return true;
            }

            VisitorLog log = new VisitorLog();
            log.setTimestamp(LocalDateTime.now());
            log.setMethod(request.getMethod());
            
            // Reconstruct full URL
            String fullUrl = request.getRequestURL().toString();
            String queryString = request.getQueryString();
            if (queryString != null) {
                fullUrl += "?" + queryString;
            }
            log.setUrl(fullUrl);

            log.setIpAddress(ip);

            log.setUserAgent(userAgent);

            // Get Username if authenticated
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                log.setUsername(auth.getName());
            }

            visitorLogService.saveLog(log);
        } catch (Exception e) {
        }

        return true;
    }
}
