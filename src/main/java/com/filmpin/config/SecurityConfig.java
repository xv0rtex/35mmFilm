package com.filmpin.config;

import com.filmpin.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomUserDetailsService userDetailsService) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/images/uploads/**").authenticated()
                .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/favicon.png", "/manifest.webmanifest", "/service-worker.js", "/robots.txt", "/sitemap.xml").permitAll()
                .anyRequest().authenticated()
            )
            .requiresChannel(channel -> channel
                .anyRequest().requiresSecure()
            )
            .formLogin((form) -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .rememberMe((remember) -> remember
                .alwaysRemember(true)
                .tokenValiditySeconds(30 * 24 * 60 * 60) // 30 days
                .key("filmpin-remember-me-key")
                .userDetailsService(userDetailsService)
                .useSecureCookie(true) // Forzar Secure para PWA en iOS
            )
            .logout((logout) -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .headers(headers -> headers
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .preload(true)
                    .maxAgeInSeconds(31536000)
                )
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
