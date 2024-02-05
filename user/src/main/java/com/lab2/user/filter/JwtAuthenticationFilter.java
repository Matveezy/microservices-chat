package com.lab2.user.filter;

import com.google.common.net.HttpHeaders;
import com.lab2.user.entity.User;
import com.lab2.user.service.JwtService;
import com.lab2.user.service.UserDetailsServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.lab2.user.util.RequestAttributeNames.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final static String BEARER = "Bearer";

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }
        final String jwt = authHeader.substring(BEARER.length() + 1);
        final String username = jwtService.extractLogin(jwt);
        if (username != null) {
            User userDetails = (User) userService.loadUserByUsername(username);
            if (userDetails == null) throw new EntityNotFoundException();
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        userDetails.getPassword(),
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // session and ip
                SecurityContextHolder.getContext().setAuthentication(authToken);
                request.setAttribute(USER_ID_ATTRIBUTE_NAME, userDetails.getId());
                request.setAttribute(USERNAME_ATTRIBUTE_NAME, userDetails.getUsername());
                request.setAttribute(AUTHORITY, userDetails.getAuthorities());
            }
        }
        filterChain.doFilter(request, response);
    }
}

