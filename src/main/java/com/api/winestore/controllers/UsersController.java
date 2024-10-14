package com.api.winestore.controllers;

import com.api.winestore.dtos.LoginDTO;
import com.api.winestore.dtos.UserDTO;
import com.api.winestore.entities.UserEntity;
import com.api.winestore.enums.RoleEnum;
import com.api.winestore.others.LoginReturnDTO;
import com.api.winestore.others.ResponseReturn;
import com.api.winestore.services.UsersService;
import com.api.winestore.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;




    @PostMapping("/save")
    public ResponseEntity<?> save(
            @RequestBody @NotNull UserDTO userDTO,
            @RequestParam(value = "authenticate", required = false, defaultValue = "false") boolean authenticate
    ) {
        if (usersService.existsByEmail(userDTO.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseReturn("Email já cadastrado", null));
        }

        var userEntity = new UserEntity();
        BeanUtils.copyProperties(userDTO, userEntity);
        userEntity.setRole(RoleEnum.ROLE_USER);

        var createdUser = usersService.save(userEntity);
        if (!authenticate) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseReturn("Usuário criado com sucesso", createdUser));
        }

        var token = jwtUtils.generateToken(createdUser.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseReturn(
                        "Usuário criado com sucesso",
                        new LoginReturnDTO(
                                createdUser.getId(),
                                token,
                                System.currentTimeMillis() + JWTUtils.expiration
                        )
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @NotNull LoginDTO loginDTO
    ) {
        var userOptional = usersService.findByEmail(loginDTO.email());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseReturn("Email não encontrado", null));
        }

        if (!passwordEncoder.matches(loginDTO.password(), userOptional.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseReturn("Senha incorreta", null));
        }

        var token = jwtUtils.generateToken(userOptional.get().getEmail());
        return ResponseEntity.ok(new ResponseReturn(
                "Login realizado com sucesso",
                new LoginReturnDTO(
                        userOptional.get().getId(),
                        token,
                        System.currentTimeMillis() + JWTUtils.expiration
                        )
        ));
    }


    // ------------------------------------------------------------------ //


    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getById(
            @PathVariable(value = "userId") UUID userId
    ) {
        var userOptional = usersService.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseReturn("Usuário não encontrado", null));
        }

        return ResponseEntity.ok(new ResponseReturn(null, userOptional.get()));
    }

}
