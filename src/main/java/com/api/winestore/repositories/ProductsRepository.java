package com.api.winestore.repositories;

import com.api.winestore.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductsRepository extends JpaRepository<ProductEntity, UUID> {

    @Query(
            value = "SELECT * FROM products WHERE status != 'DELETED'",
            nativeQuery = true
    )
    Page<ProductEntity> findAllExcludeDeleted(Pageable pageable);

    @Query(
            value = "SELECT * FROM products WHERE status != 'DELETED' AND (" +
                    "UPPER(name) LIKE UPPER(concat('%', :text, '%')) OR " +
                    "UPPER(type) LIKE UPPER(concat('%', :text, '%')) OR " +
                    "UPPER(category) LIKE UPPER(concat('%', :text, '%')) OR " +
                    "UPPER(classification) LIKE UPPER(concat('%', :text, '%')) OR " +
                    "UPPER(country) LIKE UPPER(concat('%', :text, '%')))",
            nativeQuery = true
    )
    Page<ProductEntity> findAllByText(
            @Param(value = "text") String text,
            Pageable pageable
    );

}
