package com.playdata.orderserviceback.user.dto;

import com.playdata.orderserviceback.common.entity.Address;
import com.playdata.orderserviceback.user.entity.Role;
import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResDTO {

    private String email;
    private Long id;
    private String name;
    private Role role;
    private Address address;

}


