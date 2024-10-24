package com.api.winestore.services;

import com.api.winestore.entities.HarmonizationEntity;
import com.api.winestore.repositories.HarmonizationsRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HarmonizationsService {

    private final HarmonizationsRepository harmonizationsRepository;




    public HarmonizationEntity save(HarmonizationEntity harmonization) {
        return harmonizationsRepository.save(harmonization);
    }


    // ------------------------------------------------------------------ //


    public Optional<HarmonizationEntity> findById(UUID id) {
        return harmonizationsRepository.findById(id);
    }

    public List<HarmonizationEntity> findAllByName(@NotNull String name) {
        if (name.isBlank()) name = "%";
        return harmonizationsRepository.findAllByName(name);
    }

    public Optional<HarmonizationEntity> findByName(String name) {
        return harmonizationsRepository.findByName(name);
    }

}
