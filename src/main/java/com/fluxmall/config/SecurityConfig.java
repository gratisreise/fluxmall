package com.fluxmall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringAntMatchers("/api/**")
            )
            .authorizeRequests(authorize -> authorize
                // 누구나 접근 가능한 경로
                .antMatchers(
                    "/",
                    "/member/register**",     // 회원가입 폼 + 처리
                    "/member/login**",        // 로그인 폼 + 처리
                    "/member/checkUsername**", // 아이디 중복 체크 (AJAX)
                    "/products**",            // 상품 목록, 상세, 검색 등
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico"
                ).permitAll()

                // 로그인한 사용자만 접근 가능
                .antMatchers(
                    "/member/myPage**",       // 내 정보 조회/수정
                    "/cart/**",               // 장바구니
                    "/order/**"               // 주문 관련
                ).authenticated()

                .anyRequest().authenticated()  // 나머지는 인증 필요
            )
            .formLogin(form -> form
                .loginPage("/member/login")       // 커스텀 로그인 페이지
                .loginProcessingUrl("/member/login") // POST 처리 URL
                .usernameParameter("username")    // 폼의 name 속성
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)     // 로그인 성공 후 리다이렉트
                .failureUrl("/member/login?error=true") // 실패 시
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/member/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}