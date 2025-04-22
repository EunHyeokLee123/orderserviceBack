package com.playdata.orderserviceback.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter @Setter @ToString
@NoArgsConstructor
public class CommonResDTO {

    private int statusCode;
    private String statusMessage;
    private Object result;

    public CommonResDTO(HttpStatus httpStatus, String statusMessage, Object result) {
        this.statusCode = httpStatus.value();
        this.statusMessage = statusMessage;
        this.result = result;
    }

}
