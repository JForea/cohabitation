package com.example.backend.configurations;

import com.example.backend.repositories.UserRepository;
import com.example.backend.security.JwtAuthFilter;
import com.example.backend.services.JwtService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    final UserRepository userRepository;
    final AuthenticationProvider authenticationProvider;

    public SecurityConfig(
            UserRepository userRepository,
            AuthenticationProvider authenticationProvider
    ) {
        this.userRepository = userRepository;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // TODO: DEVELOPMENT ONLY
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://192.168.*:*"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PATCH", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-Requested-With"
        ));

        config.setExposedHeaders(List.of(
                "Authorization"
        ));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService,
            @Qualifier("userDetailsService") UserDetailsService userDetailsService
    ) {
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtService, userDetailsService);

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        .requestMatchers("/api/users/auth/logout").authenticated()
                                        .requestMatchers("/api/users/auth/**").anonymous()
                                        .requestMatchers("/api/users/**").authenticated()
                                        .requestMatchers(HttpMethod.POST, "/api/apartments")
                                        .hasRole("HOUSELESS")
                                        .requestMatchers(HttpMethod.POST, "/api/apartments/join")
                                        .hasRole("HOUSELESS")
                                        .requestMatchers(HttpMethod.POST, "/api/apartments/leave")
                                        .hasAnyRole("INHABITANT", "ADMIN")
                                        .requestMatchers(HttpMethod.POST, "/api/apartments/*/rules")
                                        .hasAnyRole("ADMIN", "CREATOR")
                                        .requestMatchers(HttpMethod.POST, "/api/apartments/*/profiles/**")
                                        .hasAnyRole("ADMIN", "CREATOR")
                                        .requestMatchers(HttpMethod.DELETE, "/api/apartments/*/rules/**")
                                        .hasAnyRole("ADMIN", "CREATOR")
                                        .requestMatchers(HttpMethod.DELETE, "/api/apartments/*")
                                        .hasRole("CREATOR")
                                        .requestMatchers(HttpMethod.PATCH, "/api/apartments/*/budget")
                                        .hasAnyRole("ADMIN", "CREATOR")
                                        .requestMatchers(HttpMethod.PATCH, "/api/apartments/*/profiles/**")
                                        .hasRole("CREATOR")
                                        .requestMatchers("/api/apartments/*/code")
                                        .hasAnyRole("ADMIN", "CREATOR")
                                        .requestMatchers("/api/apartments/**")
                                        .hasAnyRole("INHABITANT", "ADMIN", "CREATOR")
                                        .anyRequest().permitAll()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).
                authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
