package com.playdata.orderserviceback.user.controller;

import com.playdata.orderserviceback.common.auth.JwtTokenProvider;
import com.playdata.orderserviceback.common.dto.CommonErrorDTO;
import com.playdata.orderserviceback.common.dto.CommonResDTO;
import com.playdata.orderserviceback.user.dto.UserLoginReqDTO;
import com.playdata.orderserviceback.user.dto.UserResDTO;
import com.playdata.orderserviceback.user.dto.UserSaveReqDTO;
import com.playdata.orderserviceback.user.entity.User;
import com.playdata.orderserviceback.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user") // user 관련 요청은 모두 /user로 시작한다.
@RequiredArgsConstructor
@Slf4j
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

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisTemplate<String, Object> redisTemplate;

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

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody UserLoginReqDTO dto){
        User user = userService.login(dto);
        // 하단의 코드가 실행되었다면 로그인 성공한 것

        // 회원정보가 일치한다면 -> 로그인 성공
        // 로그인 유지를 해주고 싶다. 백엔드는 요청이 들어왔을 때 이 사람이 이전에 로그인 성공한
        // 사람인지 알 수가 없음.
        // 징표(JWT)를 만들어주자. -> 클라이언트에게 JWT를 넘겨주자.
        
        // Access Token -> 수명 짧음
        String token = jwtTokenProvider.createToken(user.getEmail()
                , user.getRole().toString());

        // Refresh Token -> 수명 길음
        // Access Token 수명이 만료되었을 경우 Refresh Token이 유효한 경우
        // 로그인 없이 Access Token을 다시 발급해주자.
        String RefreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(),
                user.getRole().toString());

        // refreshToken을 DB에 저장하자. (redis에 저장하자.)
        // userService.saveRefreshToken(RefreshToken, user.getEmail());

        // key, value, 만료시간, 시간 단위를 넘겨주자.
        redisTemplate.opsForValue().set(
                // key
                "user:refresh:"+user.getId(),
                // value
                RefreshToken,
                // 만료 시간
                2,
                // 시간의 단위, 초,분,시,일,주 등 다양함.
                TimeUnit.MINUTES);


        // Map을 이용해서 사용자의 id와 token을 포장하자.
        // 프론트단에서 사용자가 누구인지 알게하기 위해 id를 넘겨주도록 하자.
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("token", token);
        loginInfo.put("id", user.getId());
        loginInfo.put("role", user.getRole().toString());


        CommonResDTO resDTO
                = new CommonResDTO(HttpStatus.OK,
                "로그인 성공", loginInfo);

        return new ResponseEntity<>(resDTO, HttpStatus.OK);
    }

    // 운영자용(admin) 회원 정보 조회 -> ADMIN만 회원 전체 목록 조회 가능
    // 이 메소드 호출 전에, Role에 ADMIN이 있는지 확인
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    // controller의 매개변수로 Pageable 선언하면 페이징 파라미터 처리를 쉽게 할 수 있음.
    // /list?number=1&size=20&sort=desc -> 1page라서 값은 0으로 됨.
    // 요청 시 쿼리스트링이 전달되지 않으면 기본값(0, 20, unsorted)로 처리됨.
    public ResponseEntity<?> getUserList(Pageable pageable){

        System.out.println("pageable = " + pageable);;
        // /list?number=1&size=10&sort=name,desc 요런 식으로.
        List<UserResDTO> dtoList = userService.userList(pageable);

        CommonResDTO resDTO =
                new CommonResDTO(HttpStatus.OK, "관리자용 회원 조회 성공", dtoList);

        
        // responseEntity에 전달 객체와 메시지를 한줄로 표현 가능
        return ResponseEntity.ok().body(resDTO);
    }

    // 회원 정보 조회 요청 (마이페이지) -> 로그인 한 회원만이 요청할 수 있음.
    // 일반회원용 정보 조회
    @GetMapping("/myInfo")
    public ResponseEntity<?> getMyInfo(){
        UserResDTO dto = userService.myInfo();
        CommonResDTO resDTO =
                new CommonResDTO(HttpStatus.OK, "myInfo 조회 성공", dto);


        return new ResponseEntity<>(resDTO, HttpStatus.OK);
    }

    // ACCESS_TOKEN이 만료되어 새 토큰을 요청
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> map){
        // redis에 해당 id로 조회되는 내용이 있는지 확인하자
        String id = map.get("id");
        Object obj = redisTemplate.opsForValue().get("user:refresh:" + id);


        // refresh 토큰이 만료된 경우
        if(obj == null){
            return new ResponseEntity<>(
                    new CommonErrorDTO(HttpStatus.UNAUTHORIZED,
                            "EXPIRED_RT"),
                            HttpStatus.UNAUTHORIZED);
        }
        log.info(Objects.requireNonNull(obj).toString());
        // 새로운 access token을 발급
        User foundUser = userService.findById(id);
        String newAccessToken = jwtTokenProvider.createToken(foundUser.getEmail(),
                foundUser.getRole().toString());

        Map<String, Object> info = new HashMap<>();
        info.put("token", newAccessToken);
        info.put("id", foundUser.getId());
        info.put("role", foundUser.getRole().toString());

        CommonResDTO resDTO =
                new CommonResDTO(HttpStatus.OK,
                        "새로운 access token 발급됨", info);
        return ResponseEntity.ok().body(resDTO);

    }

}
