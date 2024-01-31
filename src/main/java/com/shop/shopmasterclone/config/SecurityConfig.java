package com.shop.shopmasterclone.config;

import com.shop.shopmasterclone.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    MemberService memberService;

    /**
     * PasswordEncoder 빈으로 등록합니다.
     * 비밀번호 암호화에 사용됩니다.
     *
     * @return BCryptPasswordEncoder 객체
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .formLogin(form -> form
                        .loginPage("/members/login")
                        .defaultSuccessUrl("/")
                        .usernameParameter("email")
                        .failureUrl("/members/login/error"))
            .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                        .logoutSuccessUrl("/"));

        http.authorizeHttpRequests(request -> request
                .requestMatchers("/", "members/**", "/item/**", "/images/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated());

        return http.build();

    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return webSecurity -> webSecurity.ignoring().requestMatchers("/css/**", "/js/**", "/img/**");
    }

    /**
     * AuthenticationManager 빈을 생성합니다.
     * 이는 Spring Security 인증 메커니즘을 관리합니다.
     *
     * @param authConfig 인증 설정
     * @return AuthenticationManager 인증 관리자
     * @throws Exception 예외 처리
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
//
//    /**
//     * DaoAuthenticationProvider 빈을 생성합니다.
//     * 이는 사용자 인증 서비스와 비밀번호 인코더를 설정합니다.
//     *
//     * @return DaoAuthenticationProvider 인증 제공자
//     */
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(memberService); // 사용자 상세 서비스를 설정합니다.
//        authProvider.setPasswordEncoder(passwordEncoder()); // 비밀번호 인코더를 설정합니다.
//        return authProvider;
//    }
}

