package com.playdata.orderserviceback.product.service;


import com.playdata.orderserviceback.product.dto.ProductResDTO;
import com.playdata.orderserviceback.product.dto.ProductSaveReqDTO;
import com.playdata.orderserviceback.product.dto.ProductSearchDTO;
import com.playdata.orderserviceback.product.entity.Product;
import com.playdata.orderserviceback.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;


    public Product productCreate(ProductSaveReqDTO dto) {
        // 원본 이미지를 어딘가에 저장하고, 그 저장된 위치를 Entity에 세팅해야함.
        MultipartFile productImage = dto.getProductImage();

        // 상품을 등록하는 과정에서 이미지 이름의 중복으로 인한 충돌을 방지하기 위해
        // 랜덤한 문자열을 섞어서 충돌을 막아주자.
        String uniqueFileName
                = UUID.randomUUID() + "_" + productImage.getOriginalFilename();

        // 특정 로컬 경로에 이미지를 전송하고, 그 경로를 Entity에 세팅하자.
        File file =
                new File("C:/Users/user/Desktop/Upload/" + uniqueFileName);

        // 화면단에서 전달받은 이미지를 로컬 저장소로 보내기
        try {
            productImage.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패ㅠㅠ");
        }

        Product product = dto.toEntity();
        product.setImagePath(uniqueFileName);

        return productRepository.save(product);
    }

    public List<ProductResDTO> productList(ProductSearchDTO dto, Pageable pageable) {

        Page<Product> all;
        if(dto.getCategory() == null){
            all = productRepository.findAll(pageable);
        }
        else if(dto.getCategory().equals("category")){
            all = productRepository.findByCategoryValue(dto.getSearchName(),pageable);
        } else {
            all = productRepository.findByNameValue(dto.getSearchName(),pageable);
        }


        return all.getContent()
                .stream().map(Product::fromEntity)
                .toList();
    }
}
