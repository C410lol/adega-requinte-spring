package com.api.winestore.repositories;

import com.api.winestore.entities.WineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WinesRepository extends JpaRepository<WineEntity, UUID> {

    @Query(
            value = "SELECT * FROM wines WHERE status != 'DELETED'",
            nativeQuery = true
    )
    Page<WineEntity> findAllExcludeDeleted(Pageable pageable);

}
