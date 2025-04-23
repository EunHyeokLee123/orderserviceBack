package com.playdata.orderserviceback.product.dto;

import lombok.*;
import lombok.extern.java.Log;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResDTO {

    private Long id;

    private String name;

    private String category;

    private int price;

    private int stockQuantity;

    private String imageName;

}
