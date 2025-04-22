package com.playdata.orderserviceback.common.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter @Setter
@ToString
@NoArgsConstructor
public class CommonErrorDTO {

    private int statusCode;
    private String statusMessage;

    public CommonErrorDTO(HttpStatus httpStatus, String statusMessage) {
        this.statusCode = httpStatus.value();
        this.statusMessage = statusMessage;
    }



}
