package com.playdata.orderserviceback.ordering.repository;

import com.playdata.orderserviceback.ordering.entity.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderingRepository
        extends JpaRepository<Ordering, Long> {

}
