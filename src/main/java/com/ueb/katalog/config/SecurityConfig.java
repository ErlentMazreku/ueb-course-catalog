package com.ueb.katalog.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    private static final String LAST_PAGE_SESSION_ATTRIBUTE = "lastAuthenticatedPage";

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                     Object handler) {
                String requestUri = request.getRequestURI();
                String contextPath = request.getContextPath();
                String appPath = requestUri.substring(contextPath.length());

                if (request.getUserPrincipal() != null
                        && (appPath.equals("/") || appPath.startsWith("/courses")
                        || appPath.startsWith("/instructors"))) {
                    String lastPage = requestUri;
                    if (request.getQueryString() != null) {
                        lastPage += "?" + request.getQueryString();
                    }
                    request.getSession().setAttribute(LAST_PAGE_SESSION_ATTRIBUTE, lastPage);
                }
                return true;
            }
        });
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var logoutPost = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/logout");
        AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/css/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/courses/new", "/courses/*/edit",
                                "/instructors/new", "/instructors/*/edit").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                "/courses", "/courses/*/edit", "/courses/*/delete",
                                "/instructors", "/instructors/*/edit", "/instructors/*/delete")
                        .hasRole("ADMIN")
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
