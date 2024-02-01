package com.lab2.user.filter;

import com.google.common.net.HttpHeaders;
import com.lab2.user.entity.User;
import com.lab2.user.service.JwtService;
import com.lab2.user.service.UserService;
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
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final static String BEARER = "Bearer";

    private final JwtService jwtService;
    private final UserService userService;

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
            Optional<User> userByUsernameOptional = userService.findUserByUsername(username);
            if (userByUsernameOptional.isEmpty()) throw new EntityNotFoundException();
            User userDetails = userByUsernameOptional.get();
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        userDetails.getPassword(),
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // session and ip
                SecurityContextHolder.getContext().setAuthentication(authToken);
                request.setAttribute("userId", userDetails.getId());
                request.setAttribute("username", userDetails.getUsername());
                request.setAttribute("authorities", userDetails.getAuthorities());
            }
        }
        filterChain.doFilter(request, response);
    }
}
