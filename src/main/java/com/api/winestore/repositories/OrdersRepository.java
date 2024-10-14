package com.api.winestore.repositories;

import com.api.winestore.entities.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrdersRepository extends JpaRepository<OrderEntity, UUID> {

    @Query(
            value =
                    "SELECT * FROM orders WHERE " +
                    "CAST(order_number as VARCHAR) = :text OR " +
                    "UPPER(delivery) LIKE UPPER(CONCAT('%', :text, '%')) OR " +
                    "UPPER(payment) LIKE UPPER(CONCAT('%', :text, '%')) OR " +
                    "UPPER(status) LIKE UPPER(CONCAT('%', :text, '%'))",
            nativeQuery = true
    )
    Page<OrderEntity> findAllByText(
            @Param(value = "text") String text,
            Pageable pageable
    );

}
