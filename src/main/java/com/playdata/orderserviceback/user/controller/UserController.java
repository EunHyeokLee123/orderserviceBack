package com.playdata.orderserviceback.user.controller;

import com.playdata.orderserviceback.common.dto.CommonResDTO;
import com.playdata.orderserviceback.user.dto.UserSaveReqDTO;
import com.playdata.orderserviceback.user.entity.User;
import com.playdata.orderserviceback.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user") // user 관련 요청은 모두 /user로 시작한다.
@RequiredArgsConstructor
public class UserController {

   /*
     프론트 단에서 회원 가입 요청 보낼때 함께 보내는 데이터 (JSON) -> User DTO로 받자
     {
        name: String,
        email: String,
        password: String,
        address: {
            city: String,
            street: String,
            zipCode: String
        }
     }
     */

    // controller는 service에 의존적임.
    // 빈 등록된 서비스 객체를 자동으로 주입받자.
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> userCreate(@Valid @RequestBody UserSaveReqDTO dto) {
        // 화면단에서 전달된 데이터를 DB에 넣자
        // 혹시 이메일이 중복되었는가를 먼저 검사해야함 (UNIQUE 제약조건)
        // 중복되면 회원가입을 거절해야 함.
        // dto를 entity로 바꾸는 로직 필요
        User saved = userService.userCreate(dto);

        CommonResDTO resDTO
                = new CommonResDTO(HttpStatus.CREATED,
                "User Created", saved.getEmail());



        // ResponseEntity: 응답을 줄 때 다양한 정보를 한번에 포장해서 넘길 수 있음.
        // 요청에 따른 응답 상태 코드, 응답 헤더에 정보를 추가, 일관된 응답 처리를 제공
        return new ResponseEntity<>(resDTO, HttpStatus.CREATED);

    }

}
