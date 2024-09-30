package com.api.winestore.repositories;

import com.api.winestore.entities.GrapeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GrapesRepository extends JpaRepository<GrapeEntity, UUID> {

    @Query(
            value = "SELECT * FROM grapes WHERE UPPER(name) " +
                    "LIKE UPPER(concat('%', :name, '%'))",
            nativeQuery = true
    )
    List<GrapeEntity> findAllByName(@Param(value = "name") String name);

    @Query(
            value = "SELECT * FROM grapes WHERE UPPER(name) = UPPER(:name)",
            nativeQuery = true
    )
    Optional<GrapeEntity> findByName(@Param(value = "name") String name);

}
