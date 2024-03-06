package ru.kor.testworkoutapp.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.kor.testworkoutapp.service.JwtService;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // getting the token from the request header if we have it
        final String authorizationHeader = request.getHeader("Authorization");
        final String userEmail;
        final String jwt;
        // if we don't have a token then we send to DS without any checks and authentications
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // if we have a token then we extract the user from it
        jwt = authorizationHeader.substring(7);
        // getting the user from the token
        userEmail = jwtService.extractUsername(jwt);
        // if we have a user, and it's not authenticated, yet then we authenticate it and send to DS
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // getting the user from DB and checking the token validity
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // sending to DS
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                // setting the details of the user in the token
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // setting the user
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        // next filter if we have it
        filterChain.doFilter(request, response);
    }
}
