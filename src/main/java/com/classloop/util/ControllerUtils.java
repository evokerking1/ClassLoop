package com.classloop.util;

import jakarta.servlet.http.HttpServletRequest;

public class ControllerUtils {
    
    public static String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return "";
    }
}
