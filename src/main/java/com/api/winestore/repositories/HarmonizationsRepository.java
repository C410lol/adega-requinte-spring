package com.api.winestore.repositories;

import com.api.winestore.entities.HarmonizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HarmonizationsRepository extends JpaRepository<HarmonizationEntity, UUID> {

    @Query(
            value =
                    "SELECT * FROM harmonizations " +
                    "WHERE UPPER(name) LIKE UPPER(CONCAT('%', :name, '%'))",
            nativeQuery = true
    )
    List<HarmonizationEntity> findAllByName(@Param(value = "name") String name);

    @Query(
            value = "SELECT * FROM harmonizations WHERE UPPER(name) = UPPER(:name)",
            nativeQuery = true
    )
    Optional<HarmonizationEntity> findByName(@Param(value = "name") String name);

}
