package com.playdata.orderserviceback.common.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 클라이언트가 전송한 토큰을 검사하는 필터
// spring security에 등록해서 사용할 것임.
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 우리가 만드는 이 필터가 요청 한번 당 자동으로 동작할 수 있게끔
        // OncePerRequestFilter를 상속받음.
        // 메소드 내에는 필터가 해야 할 로직을 작성하면 됨.

        // 요청과 함께 전달된 jwt를 얻어와야 함.
        // jwt는 클라이언트 단에서 요청 헤더에 담겨져서 전달됨.


    }
}
