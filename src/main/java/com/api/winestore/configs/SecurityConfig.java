package com.api.winestore.configs;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.
                cors(Customizer.withDefaults()) //Using custom configurations for CORS
                .csrf(AbstractHttpConfigurer::disable) //Disable CSRF
                .authorizeHttpRequests(auth -> //Authorizing some endpoints
                        auth.requestMatchers(
                                "users/**",
                                "/wines/**",
                                "/orders/**",
                                "/addresses/**",
                                "/grapes/**"
                                )
                                .permitAll().anyRequest().authenticated())
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() { //Custom configurations for CORS
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200", "http://192.168.1.103:4200")); //Allowing all origins
        corsConfiguration.setAllowedHeaders(List.of("*")); //Allowing headers
        corsConfiguration.setAllowedMethods(List.of("POST", "GET", "PUT", "DELETE")); //Allowing methods
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }

}
