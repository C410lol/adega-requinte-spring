package com.api.winestore.services;

import com.api.winestore.entities.CountryEntity;
import com.api.winestore.repositories.CountriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CountriesService {

    private final CountriesRepository countriesRepository;




    public Optional<CountryEntity> findById(UUID id) {
        return countriesRepository.findById(id);
    }

}
