package com.example.taskmanagementsystem.configurations;

import com.example.taskmanagementsystem.securities.jwt.JwtAuthenticationEntryPoint;
import com.example.taskmanagementsystem.securities.jwt.JwtTokenFilter;
import com.example.taskmanagementsystem.services.TokenService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")},
        security = {@SecurityRequirement(name = "Authentication") })
@SecurityScheme(name = "Authentication", scheme = "bearer", bearerFormat = "JWT",
        type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
@RequiredArgsConstructor
public class SecurityConfiguration {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           TokenService tokenService,
                                           JwtTokenFilter jwtTokenFilter,
                                           JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint)
            throws Exception {
        http
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/openapi-docs", "/openapi-docs/**", "/swagger-ui/**", "/proxy/**",
                                        "/api/auth/**", "/api/app/**", "/favicon.ico", "/error")
                        .permitAll().anyRequest().authenticated()
                ).exceptionHandling(customizer ->
                        customizer.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                ).cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(logout -> logout.logoutSuccessHandler((request, response, authentication) ->
                        tokenService.delete(jwtTokenFilter.getToken(request))
                ))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
