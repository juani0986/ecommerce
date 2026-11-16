package com.parquesoft.ecommerce.repository;

import com.parquesoft.ecommerce.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByCustomerId(Long customerId);

    List<OrderEntity> findByStatus(String status);

    @Query(" SELECT o FROM OrderEntity o WHERE o.totalAmount >= :minAmount ORDER BY o.totalAmount DESC")
    List<OrderEntity> findOrdersAboveAmount(@Param("minAmount") double minAmount);
}
