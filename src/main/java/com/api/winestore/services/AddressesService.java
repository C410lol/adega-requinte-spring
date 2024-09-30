package com.api.winestore.services;

import com.api.winestore.entities.AddressEntity;
import com.api.winestore.repositories.AddressesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressesService {

    private final AddressesRepository addressesRepository;




    public AddressEntity save(AddressEntity address) {
        return addressesRepository.save(address);
    }


    // ------------------------------------------------------------------ //


    public Optional<AddressEntity> findById(UUID id) {
        return addressesRepository.findById(id);
    }


}
