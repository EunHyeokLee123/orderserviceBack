package com.playdata.orderserviceback.user.entity;

import com.playdata.orderserviceback.common.entity.Address;
import jakarta.persistence.*;
import lombok.*;

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

}
