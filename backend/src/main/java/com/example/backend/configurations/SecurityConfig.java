package com.example.backend.configurations;

import com.example.backend.repositories.UserRepository;
import com.example.backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    final JwtAuthFilter jwtAuthFilter;
    final UserRepository userRepository;
    final AuthenticationProvider authenticationProvider;

    public SecurityConfig(
            JwtAuthFilter jwtAuthFilter,
            UserRepository userRepository,
            AuthenticationProvider authenticationProvider
    ) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userRepository = userRepository;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // TODO: DEVELOPMENT ONLY
        corsConfiguration.setAllowedOriginPatterns(List.of(
                "http://localhost:[*]",
                "http://127.0.0.1:[*]",
                "http://192.168.*:[*]"
        ));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        corsConfiguration.setExposedHeaders(List.of("Authorization"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .anonymous(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        .requestMatchers("/api/users/auth/**").permitAll()
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
                                        .requestMatchers(HttpMethod.PATCH, "/api/apartments/*/profiles")
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
