package com.api.winestore.repositories;

import com.api.winestore.entities.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CountriesRepository extends JpaRepository<CountryEntity, UUID> {
}
