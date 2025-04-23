package com.playdata.orderserviceback.user.entity;

import com.playdata.orderserviceback.common.entity.Address;
import com.playdata.orderserviceback.user.dto.UserResDTO;
import jakarta.persistence.*;
import lombok.*;


// entity에 setter를 구현하지 않는 이유는 entity 자체가 DB와 연동하기 위한 객체이기 때문
// DB에 삽입되는 데이터, 또는 DB에서 조회되는 데이터는 그 자체로 사용하고 수정되지 않게끔
// setter를 사용하지 않는 것을 권장
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="tbl_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Embedded // @Embeddable로 선언한 값을 대입시킴. (기본 생성자 필수!)
    private Address address;

    @Enumerated(EnumType.STRING)
    @Builder.Default // build 패턴 사용시 전달한 값으로 세팅하기 위한 어노테이션
    private Role role = Role.USER; // @Builder.Default이면 기본 초기값을 넣어줘야 함.


    // DTO에 Entity 변환 메소드가 있는 것처럼
    // Entity도 응답용 DTO 변환 메소드를 세팅해서 언제든 변환이 자유롭도록 작성.
    public UserResDTO fromEntity(){
        return UserResDTO.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .address(this.address)
                .role(this.role)
                .build();
    }


}
