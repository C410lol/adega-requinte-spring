package com.api.winestore.services;

import com.api.winestore.dtos.LoginDTO;
import com.api.winestore.entities.UserEntity;
import com.api.winestore.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final JWTService jwtService;




    public UserEntity save(UserEntity userEntity) {
        return usersRepository.save(userEntity);
    }


    // ------------------------------------------------------------------ //


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


    public String authenticateUser(@NotNull LoginDTO loginDTO) {
        var userOptional = usersRepository.findByEmail(loginDTO.email());
        if (userOptional.isEmpty()) return null;

        if (!userOptional.get().getPassword().equals(loginDTO.password())) return null;

        return jwtService.generateToken(userOptional.get().getId());
    }

}
