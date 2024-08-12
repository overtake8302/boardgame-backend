//package com.elice.boardgame.common.config;

//import com.elice.boardgame.auth.jwt.JWTFilter;
//import com.elice.boardgame.auth.jwt.JWTUtil;
//import com.elice.boardgame.auth.jwt.LoginFilter;
//import com.elice.boardgame.auth.repository.UserRepository;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final AuthenticationConfiguration authenticationConfiguration;
//    private final JWTUtil jwtUtil;
//    private final UserRepository userRepository; // Add UserRepository
//
//    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, UserRepository userRepository) {
//        this.authenticationConfiguration = authenticationConfiguration;
//        this.jwtUtil = jwtUtil;
//        this.userRepository = userRepository; // Initialize UserRepository
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager() throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http.csrf((auth) -> auth.disable());
//        http.formLogin((auth) -> auth.disable());
//        http.httpBasic((auth) -> auth.disable());
//
//        http.authorizeHttpRequests((auth) -> auth
//                .requestMatchers("/login", "/", "/join").permitAll()
//                .requestMatchers("/admin").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.GET, "/game/**").permitAll()
//                .requestMatchers(HttpMethod.POST, "/game/view").permitAll()
//                .requestMatchers(HttpMethod.POST, "/game/**").authenticated()
//                .requestMatchers(HttpMethod.PUT, "/game/**").authenticated()
//                .requestMatchers(HttpMethod.DELETE, "/game/**").authenticated()
//                .anyRequest().permitAll());
//
//        // 로그아웃 설정
//        http.logout(logout -> {
//            logout.logoutUrl("logout")  // 로그아웃을 위한 URL
//                    .logoutSuccessUrl("/login") // 로그아웃 성공 후 이동할 URL
//                    .invalidateHttpSession(true) // 세션 무효화
//                    .deleteCookies("JSESSIONID"); // 쿠키 삭제
//        });
//
//        http.addFilterBefore(new JWTFilter(jwtUtil, userRepository), UsernamePasswordAuthenticationFilter.class)
//                .addFilterAt(new LoginFilter(authenticationManager(), jwtUtil), UsernamePasswordAuthenticationFilter.class);
//
//        http.sessionManagement((session) -> session
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        return http.build();
//    }
//}

package com.elice.boardgame.common.config;

import com.elice.boardgame.auth.OAuth2.CustomSuccessHandler;
import com.elice.boardgame.auth.OAuth2.service.CustomOAuth2UserService;
import com.elice.boardgame.auth.jwt.JWTFilter;
import com.elice.boardgame.auth.jwt.JWTUtil;
import com.elice.boardgame.auth.jwt.LoginFilter;
import com.elice.boardgame.auth.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final UserRepository userRepository;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,
                          JWTUtil jwtUtil,
                          CustomOAuth2UserService customOAuth2UserService,
                          CustomSuccessHandler customSuccessHandler,
                          UserRepository userRepository) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.userRepository = userRepository;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Disable CSRF and other default configurations
        http
                .csrf((auth) -> auth.disable())
                .formLogin((auth) -> auth.disable())
                .httpBasic((auth) -> auth.disable());

        // Add JWT filter
        http
                .addFilterBefore(new JWTFilter(jwtUtil, userRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new LoginFilter(authenticationManager(), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // Configure OAuth2 login
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                );

        // Authorization rules
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "/join").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        // Session management configuration
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}