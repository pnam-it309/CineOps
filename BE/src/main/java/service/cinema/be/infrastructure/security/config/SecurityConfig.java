package service.cinema.be.infrastructure.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import service.cinema.be.infrastructure.security.filter.TokenAuthenticationFilter;
import service.cinema.be.infrastructure.security.oauth2.CustomOAuth2UserService;
import service.cinema.be.infrastructure.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import service.cinema.be.infrastructure.security.oauth2.OAuth2AuthenticationFailureHandler;
import service.cinema.be.infrastructure.security.oauth2.OAuth2AuthenticationSuccessHandler;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Value("${frontend.url:*}")
    private String allowedOrigin;

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder,
                                                       org.springframework.security.core.userdetails.UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return new org.springframework.security.authentication.ProviderManager(provider);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigin));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.cors(c -> c.configurationSource(corsConfigurationSource()));
        http.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.formLogin(fl -> fl.disable());
        http.httpBasic(hb -> hb.disable());

        http.authorizeHttpRequests(auth -> auth
                // Public access - no authentication required
                .requestMatchers(
                        "/",
                        "/error",
                        "/favicon.ico",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                ).permitAll()
                
                // Authentication endpoints
                .requestMatchers(
                        service.cinema.be.infrastructure.constant.MappingConstants.API_AUTH_PREFIX + "/**",
                        service.cinema.be.infrastructure.constant.MappingConstants.API_OAUTH2_PREFIX + "/**"
                ).permitAll()
                
                // Public content (read-only, no auth)
                .requestMatchers(
                        org.springframework.http.HttpMethod.GET,
                        service.cinema.be.infrastructure.constant.MappingConstants.API_PUBLIC_PREFIX + "/**"
                ).permitAll()
                
                // Customer endpoints - CUSTOMER or ADMIN only
                .requestMatchers(
                        service.cinema.be.infrastructure.constant.MappingConstants.API_CUSTOMER_PREFIX + "/**"
                ).hasAnyRole("CUSTOMER", "ADMIN")
                
                // Staff endpoints - STAFF or ADMIN only
                .requestMatchers(
                        service.cinema.be.infrastructure.constant.MappingConstants.API_STAFF_PREFIX + "/**"
                ).hasAnyRole("STAFF", "ADMIN")
                
                // Admin endpoints - ADMIN only
                .requestMatchers(
                        service.cinema.be.infrastructure.constant.MappingConstants.API_ADMIN_PREFIX + "/**"
                ).hasRole("ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
        );

        // Add our custom Token based authentication filter
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // OAuth2 Login Configuration
        http.oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization -> authorization
                        .baseUri("/oauth2/authorize")
                        .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                )
                .redirectionEndpoint(redirection -> redirection
                        .baseUri("/oauth2/callback/*")
                )
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService)
                )
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
        );

        return http.build();
    }
}
