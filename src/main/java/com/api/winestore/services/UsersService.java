package com.api.winestore.services;

import com.api.winestore.entities.UserEntity;
import com.api.winestore.enums.RoleEnum;
import com.api.winestore.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();




    public UserEntity save(@NotNull UserEntity userEntity) {
        if (userEntity.getId() == null) userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        return usersRepository.save(userEntity);
    }


    // ------------------------------------------------------------------ //


    public List<UserEntity> findAllByRoleAndText(@NotNull RoleEnum roleEnum, String text) {
        return usersRepository.findAllByRoleAndText(roleEnum.name(), text);
    }

    public Optional<UserEntity> findById(UUID id) {
        return usersRepository.findById(id);
    }

    public Optional<UserEntity> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }


    // ------------------------------------------------------------------ //




}
