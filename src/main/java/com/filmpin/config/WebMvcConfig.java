package com.filmpin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.CacheControl;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private VisitorLogInterceptor visitorLogInterceptor;
    
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(visitorLogInterceptor)
                .addPathPatterns("/**") // Log all paths
                .excludePathPatterns("/css/**", "/js/**", "/favicon.png", "/manifest.webmanifest", "/service-worker.js", "/robots.txt", "/sitemap.xml"); // Except static resources
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Disabled static serving of uploads to enforce security via Controller
        // String location = "file:" + (uploadDir.endsWith("/") ? uploadDir : uploadDir + "/");
        // registry.addResourceHandler("/images/uploads/**")
        //         .addResourceLocations(location)
        //         .setCacheControl(CacheControl.noCache().cachePrivate());
    }
}
