package com.api.winestore.repositories;

import com.api.winestore.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductsRepository extends JpaRepository<ProductEntity, UUID> {

    @Query(
            value = "SELECT * FROM products WHERE status != 'DELETED'",
            nativeQuery = true
    )
    Page<ProductEntity> findAllExcludeDeleted(Pageable pageable);

    @Query(
            value = "SELECT products.* FROM products " +
                    "LEFT JOIN countries ON products.country_id = countries.id " +
                    "WHERE products.status != 'DELETED' AND (" +
                    "UPPER(products.name) LIKE UPPER(concat('%', :text, '%')) OR " +
                    "UPPER(products.type) LIKE UPPER(concat('%', :text, '%')) OR " +
                    "UPPER(products.category) LIKE UPPER(concat('%', :text, '%')) OR " +
                    "UPPER(products.classification) LIKE UPPER(concat('%', :text, '%')) OR " +
                    "UPPER(countries.name) LIKE UPPER(concat('%', :text, '%'))) " +
                    "order by products.name asc",
            nativeQuery = true
    )
    List<ProductEntity> findAllByText(
            @Param(value = "text") String text
    );

}
