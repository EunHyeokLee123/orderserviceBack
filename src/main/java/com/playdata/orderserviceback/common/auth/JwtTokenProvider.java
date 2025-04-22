package com.playdata.orderserviceback.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
// 역할: 토큰을 발급하고, 서명의 위조를 검사해주는 객체
public class JwtTokenProvider {

    // yml 파일에 있는 설정값 가져오기
    // 서명에 사용할 값 (512bit 이상의 랜덤 문자열을 권장)
    @Value("${jwt.secretKey}") // lombok아니고 springframework임.
    private String secretKey;


    @Value("${jwt.expiration}")
    private int expiration;

    // 토큰 생성 메서드
     /*
            {
                "iss": "서비스 이름(발급자)",
                "exp": "2023-12-27(만료일자)",
                "iat": "2023-11-27(발급일자)",
                "email": "로그인한 사람 이메일",
                "role": "Premium"
                ...
                == 서명
            }
     */

    public String createToken(String email, String role){
        // Claims: 페이로드에 들어갈 사용자 정보
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date();


        return Jwts.builder()
                .setClaims(claims)
                // 발행시간
                .setIssuedAt(now)
                // 만료시간
                // 현재 시간 밀리초에 30분을 더한 시간 만큼 만료시간으로 세팅
                // 밀리초로 리턴되기에 밀리초로 환산해서 계산해야 함.
                .setExpiration(new Date(now.getTime() + expiration * 60 * 1000))
                // 서명을 어떤 알고리즘으로 암호화 할 지
                .signWith(SignatureAlgorithm.HS512, secretKey)
                // 위의 값들을 String으로 compact화 해서 리턴.
                .compact();
    }

}
