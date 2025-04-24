package com.playdata.orderserviceback.ordering.service;

import com.playdata.orderserviceback.common.auth.TokenUserInfo;
import com.playdata.orderserviceback.ordering.dto.OrderingSaveReqDTO;
import com.playdata.orderserviceback.ordering.entity.OrderDetail;
import com.playdata.orderserviceback.ordering.entity.Ordering;
import com.playdata.orderserviceback.ordering.repository.OrderingRepository;
import com.playdata.orderserviceback.product.entity.Product;
import com.playdata.orderserviceback.product.repository.ProductRepository;
import com.playdata.orderserviceback.user.entity.User;
import com.playdata.orderserviceback.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class OrderingService {

    private final UserRepository userRepository;
    private final OrderingRepository orderingRepository;
    private final ProductRepository productRepository;

    public Ordering createOrder(List<OrderingSaveReqDTO> dtoList,
                                TokenUserInfo userInfo) {

        // Ordering 객체를 생성하기 위해 회원정보를 얻어오자
        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        // Ordering 객체를 생성하자
        Ordering ordering = Ordering.builder()
                .user(user)
                // 주문 상세 들어가기 전
                .orderDetails(new ArrayList<>())
                .build();

        // 주문 상세 내역에 대한 처리를 반복문으로 수행
        for (OrderingSaveReqDTO dto : dtoList) {
            // dto 안에 있는 productId를 통해 product를 얻어오자.
            Product product = productRepository.findById(dto.getProductId()).orElseThrow(() ->
                    new EntityNotFoundException("Not found Product"));

            // 재고가 주문수량보다 많은지 확인 (주문의 유효성 확인)
            int quantity = dto.getProductQuantity();
            // 유효하지 않은 주문
            if(product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException("재고 부족");
            }
            // 재고가 부족하지 않은 경우
            // 재고 - 주문 수량으로 재고 최신화

            product.setStockQuantity(product.getStockQuantity() - quantity);

            // OrderDetail 생성
            OrderDetail detail = OrderDetail.builder()
                    .product(product)
                    .quantity(quantity)
                    .ordering(ordering)
                    .build();

            // ordering에 있는 orderDetail list에 주문 상세 내역 추가
            // cascadeType이 PERSIST라서 ordering을 save하면 orderDetail도 자동으로 save됨
            ordering.getOrderDetails().add(detail);

        }

        return orderingRepository.save(ordering);

    }
}
