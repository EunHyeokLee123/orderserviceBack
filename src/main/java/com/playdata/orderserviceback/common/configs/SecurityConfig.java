package com.playdata.orderserviceback.common.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 설정파일인 컴포넌트
@EnableWebSecurity
@EnableMethodSecurity // 권한 검사를 Controller의 메소드에서 전역적으로 수행하기 위한 설정
public class SecurityConfig {

    // security 기본 설정 (권한 처리, 초기 로그인 화면 없애기 등등)
    @Bean // 이 메소드가 리턴하는 시큐리티 설정을 빈으로 등록하겠다.
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 스프링 시큐리티에서 기본으로 제공하는 CSRF 토큰 공격을 방지하기 위한 장치 해제.
        // CSRF(Cross Site Request Forgery) 사이트 간 요청 위조
        http.csrf(AbstractHttpConfigurer::disable);

        // 세션 관리 상태를 사용하지 않고
        // STATELESS한 토큰을 사용하겠다. (정보를 유지하지 않는)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) );

        // 요청 권한 설정 (URL의 종류에 따라 권한 검사 여부를 설정)
        http.authorizeHttpRequests(auth -> {
            // 권한 검사하지 말라는 설정
           auth.requestMatchers("/user/create", "/user/doLogin").permitAll()
            // 나머지 요청을 권한 검사를 진행하라는 설정
           .anyRequest().authenticated();
        });

        // "/user/create", "/user/doLogin"은 인증검사가 필요없고
        // 나머지 요청들은 권한 검사가 필요하다고 세팅했음.
        // 권한 검사가 필요한 요청들을 어떤 filter로 검사할지를 추가해주면 됨.


    }

}
