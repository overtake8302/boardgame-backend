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

//import com.elice.boardgame.auth.OAuth2.CustomSuccessHandler;
//import com.elice.boardgame.auth.OAuth2.service.CustomOAuth2UserService;

import com.elice.boardgame.auth.jwt.JWTFilter;
import com.elice.boardgame.auth.jwt.JWTUtil;
import com.elice.boardgame.auth.jwt.LoginFilter;
import com.elice.boardgame.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    //    private final CustomOAuth2UserService customOAuth2UserService;
//    private final CustomSuccessHandler customSuccessHandler;
    private final UserRepository userRepository;  // 필드로 추가

    // 생성자
    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil,
//                          CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler,
        UserRepository userRepository) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
//        this.customOAuth2UserService = customOAuth2UserService;
//        this.customSuccessHandler = customSuccessHandler;
        this.userRepository = userRepository;  // 필드 초기화
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

        // CSRF 및 기본 설정 비활성화
        http
            .csrf((auth) -> auth.disable())
            .formLogin((auth) -> auth.disable())
            .httpBasic((auth) -> auth.disable());

        //cors설정
        /*http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                }));*/

        // JWT 필터 추가
        http
            .addFilterBefore(new JWTFilter(jwtUtil, userRepository),
                UsernamePasswordAuthenticationFilter.class)
            .addFilterAt(new LoginFilter(authenticationManager(), jwtUtil),
                UsernamePasswordAuthenticationFilter.class);

//        // OAuth2 로그인 구성
//        http
//                .oauth2Login((oauth2) -> oauth2
//                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
//                                .userService(customOAuth2UserService))
//                        .successHandler(customSuccessHandler)
//                );

        // 권한 규칙 설정
        http
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers(HttpMethod.GET, "/game/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/game/view").permitAll()
                .requestMatchers(HttpMethod.POST, "/game/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/game/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/game/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/post/like").authenticated()
                .requestMatchers(HttpMethod.POST, "/post/insert").authenticated()
                .requestMatchers(HttpMethod.GET, "/post/*/editok").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/post/*/delete").authenticated()
                .requestMatchers(HttpMethod.GET, "/admin-check").authenticated()
                .requestMatchers(HttpMethod.POST, "/user/logout").authenticated()
                .requestMatchers(HttpMethod.GET, "/user/check").authenticated()
                .requestMatchers(HttpMethod.PUT, "/users/withdraw").authenticated()
                .requestMatchers(HttpMethod.PUT, "/users/update").authenticated()
                .requestMatchers(HttpMethod.GET, "/users/my/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/report/send").authenticated()
                .requestMatchers(HttpMethod.GET, "/recommend/**").authenticated()
                .requestMatchers(HttpMethod.POST, "genre").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/genre/*").authenticated()
                .requestMatchers(HttpMethod.PUT, "/genre/*").authenticated()
                .requestMatchers(HttpMethod.PUT, "/comments/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/comments/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/comments/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/social/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/social/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/report").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/report").hasRole("ADMIN")
                .anyRequest().permitAll()
            );

        // 세션 관리 설정
        http
            .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}
