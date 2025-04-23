package com.playdata.orderserviceback.user.service;


import com.playdata.orderserviceback.common.auth.TokenUserInfo;
import com.playdata.orderserviceback.user.dto.UserLoginReqDTO;
import com.playdata.orderserviceback.user.dto.UserResDTO;
import com.playdata.orderserviceback.user.dto.UserSaveReqDTO;
import com.playdata.orderserviceback.user.entity.User;
import com.playdata.orderserviceback.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Service로 Bean 등록(Component와 동일하지만, 의미를 명시)
@Service
@RequiredArgsConstructor
public class UserService {

    // service는 repository에 의존하고 있음 -> repository의 기능을 써야 함.
    // repository 객체를 자동으로 주입받자. (JPA가 만들어서 컨테이너에 등록해 놓음.)
    private final UserRepository userRepository;

    // controller가 이 메소드를 호출할 것임.
    // 전달받은 DTO를 매개변수로 넘길 것임.
    public User userCreate(UserSaveReqDTO dto) {
        Optional<User> foundEmail = userRepository.findByEmail(dto.getEmail());
        // 이미 존재하는 이메일인 경우 -> 회원가입 불가
        if (foundEmail.isPresent()) {
            // 이미 존재하는 이메일이라는 에러를 발생 -> controller가 이 에러를 처리
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 이메일 중복 안됨 -> 회원가입 진행
        // dto를 entity로 변환하는 로직
        User user = dto.toEntity();
        User saved = userRepository.save(user);
        return saved;

    }

    public User login(UserLoginReqDTO dto) {
        // 이메일로 user 조회하기
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("User nt Found!")
        );

        // 비밀번호 확인하기
        if(!user.getPassword().equals(dto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    // myPage에서 회원정보를 불러오는 메소드
    public UserResDTO myInfo() {
        // Security 컨테이너에 있는 User 정보를 가져오자
        TokenUserInfo userInfo = 
                // 필터에서 세팅한 시큐리티 인증 정보를 불러오는 메소드
                (TokenUserInfo) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("User nt Found!")
        );

        return user.fromEntity();

    }
}
