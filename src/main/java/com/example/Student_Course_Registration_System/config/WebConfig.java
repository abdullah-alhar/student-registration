package com.example.Student_Course_Registration_System.config;

import com.example.Student_Course_Registration_System.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    // Websites allowed to call this backend from the browser (comma separated).
    // Set with the ALLOWED_ORIGINS environment variable.
    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/", "/error", "/logout", "/health", "/health/**",
                        "/css/**", "/js/**", "/images/**", "/uploads/**");
    }

    // CORS: lets the GitHub Pages site (https://abdullah-alhar.github.io) call
    // the backend, e.g. the /health check on the "Server is waking up" page.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "HEAD", "OPTIONS")
                .maxAge(3600);
    }
}
