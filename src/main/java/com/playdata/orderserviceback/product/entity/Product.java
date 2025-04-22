package com.playdata.orderserviceback.product.entity;


import com.playdata.orderserviceback.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="tbl_product")
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    private int price;

    private int stockQuantity;

    private String imagePath;



}
