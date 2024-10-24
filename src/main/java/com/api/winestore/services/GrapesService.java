package com.api.winestore.services;

import com.api.winestore.entities.GrapeEntity;
import com.api.winestore.repositories.GrapesRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrapesService {

    private final GrapesRepository grapesRepository;




    public GrapeEntity save(GrapeEntity grape) {
        return grapesRepository.save(grape);
    }


    // ------------------------------------------------------------------ //


    public Optional<GrapeEntity> findById(UUID id) {
        return grapesRepository.findById(id);
    }

    public List<GrapeEntity> findAllByName(@NotNull String name) {
        if (name.isBlank()) name = "%";
        return grapesRepository.findAllByName(name);
    }

    public Optional<GrapeEntity> findByName(String name) {
        return grapesRepository.findByName(name);
    }

}
