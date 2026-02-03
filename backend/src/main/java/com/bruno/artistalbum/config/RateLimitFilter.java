package com.bruno.artistalbum.config;

import com.bruno.artistalbum.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, UserRequestCounter> requestCounts = new ConcurrentHashMap<>();

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientId = getClientId(request);

        if (isRateLimited(clientId)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Muitas requisicoes. Tente novamente mais tarde.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            try {
                return tokenService.getSubject(token.replace("Bearer ", ""));
            } catch (Exception e) {
                // Token invalido, usa IP
            }
        }
        return request.getRemoteAddr();
    }

    private boolean isRateLimited(String clientId) {
        long now = System.currentTimeMillis();
        requestCounts.putIfAbsent(clientId, new UserRequestCounter(now));

        UserRequestCounter counter = requestCounts.get(clientId);

        synchronized (counter) {
            if (now - counter.startTime > 60000) {
                counter.startTime = now;
                counter.count.set(0);
            }

            if (counter.count.incrementAndGet() > 10) {
                return true;
            }
        }

        return false;
    }

    private static class UserRequestCounter {
        long startTime;
        AtomicInteger count = new AtomicInteger(0);

        UserRequestCounter(long startTime) {
            this.startTime = startTime;
        }
    }
}
