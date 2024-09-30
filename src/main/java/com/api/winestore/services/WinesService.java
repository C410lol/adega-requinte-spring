package com.api.winestore.services;

import com.api.winestore.entities.WineEntity;
import com.api.winestore.repositories.WinesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WinesService {

    private final WinesRepository winesRepository;




    public WineEntity save(WineEntity wineEntity) {
        return winesRepository.save(wineEntity);
    }


    // ------------------------------------------------------------------ //


    public Page<WineEntity> findAll(Pageable pageable) {
        return winesRepository.findAllExcludeDeleted(pageable);
    }

    public Optional<WineEntity> findById(UUID id) {
        return winesRepository.findById(id);
    }


    // ------------------------------------------------------------------ //


    public void deleteById(UUID productId) {
        winesRepository.deleteById(productId);
    }

}
