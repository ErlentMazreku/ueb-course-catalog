package com.ueb.katalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var logoutPost = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/logout");
        AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

        // Në fazën 1, çdo përdorues i loguar ka qasje në të gjitha faqet.
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/css/**", "/error").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/courses", true)
                        .failureUrl("/login?error"))
                .logout(logout -> logout
                        // Filtri i çkyçjes ekzekutohet para kontrollit të qasjes.
                        .logoutRequestMatcher(request -> logoutPost.matches(request)
                                && trustResolver.isAuthenticated(SecurityContextHolder.getContext().getAuthentication()))
                        .logoutSuccessUrl("/login?logout"));

        // CSRF mbetet aktiv; format POST dërgojnë token-in përmes Thymeleaf.
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(
            @Value("${USER_PASSWORD}") String userPassword,
            @Value("${ADMIN_PASSWORD}") String adminPassword,
            PasswordEncoder passwordEncoder) {
        // Fjalëkalimet lexohen nga konfigurimi dhe kodohen para ruajtjes në memorie.
        return new InMemoryUserDetailsManager(
                User.withUsername("user")
                        .password(passwordEncoder.encode(userPassword))
                        .roles("USER")
                        .build(),
                User.withUsername("admin")
                        .password(passwordEncoder.encode(adminPassword))
                        .roles("ADMIN", "USER")
                        .build());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
