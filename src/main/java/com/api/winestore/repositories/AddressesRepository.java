package com.api.winestore.repositories;

import com.api.winestore.entities.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AddressesRepository extends JpaRepository<AddressEntity, UUID> {
}
