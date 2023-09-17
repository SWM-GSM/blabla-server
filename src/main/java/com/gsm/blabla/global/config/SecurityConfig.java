package com.gsm.blabla.global.config;

import com.gsm.blabla.jwt.JwtAccessDeniedHandler;
import com.gsm.blabla.jwt.JwtAuthenticationEntryPoint;
import com.gsm.blabla.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // h2 database 테스트가 원활하도록 관련 API 들은 전부 무시
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
            .requestMatchers( "/swagger-ui/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF 설정 Disable
        http
            .csrf(AbstractHttpConfigurer::disable)

            // exception handling 할 때 우리가 만든 클래스를 추가
            .exceptionHandling(exceptHandling ->
                exceptHandling
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .accessDeniedHandler(jwtAccessDeniedHandler)
                )

            .headers(headers ->
                headers
                    .frameOptions((FrameOptionsConfig::disable))
            )

            // 시큐리티는 기본적으로 세션을 사용
            // 여기서는 세션을 사용하지 않기 때문에 세션 설정을 Stateless 로 설정
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 토큰이 없는 상태에서 요청이 가능한 API는 permitAll 설정
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers(
                        "/oauth/**",
                        "/members/nicknames/**",
                        "/admin/**"
                    ).permitAll()
                    .requestMatchers(PathRequest.toH2Console()).permitAll() // H2-console 화면을 사용하기 위해
                    .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())

            // JwtFilter 를 addFilterBefore 로 등록했던 JwtSecurityConfig 클래스를 적용
            .apply(new JwtSecurityConfig(tokenProvider));

        return http.build();
    }
}
