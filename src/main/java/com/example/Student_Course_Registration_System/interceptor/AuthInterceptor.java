package com.example.Student_Course_Registration_System.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String path = request.getRequestURI();

        if (path.startsWith("/css") || path.startsWith("/js") || path.startsWith("/images") || 
            path.equals("/login") || path.equals("/") || path.equals("/error") || path.startsWith("/logout")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userRole") == null) {
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}
