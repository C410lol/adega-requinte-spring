package com.api.winestore.configs;

import com.api.winestore.entities.UserEntity;
import com.api.winestore.enums.RoleEnum;
import com.api.winestore.services.UsersService;
import com.api.winestore.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final UsersService usersService;
    private final JWTUtils jwtUtils;





    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (
                authorization != null &&
                !authorization.isBlank() &&
                authorization.startsWith("Bearer ")
        ) {
            String token = authorization.substring(7);
            Optional<UserEntity> optionalUser = usersService.findByEmail(jwtUtils.getEmailByToken(token));
            if (optionalUser.isPresent()) {
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        optionalUser.get(),
                        null,
                        List.of(new SimpleGrantedAuthority(optionalUser.get().getRole().name()))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

}
