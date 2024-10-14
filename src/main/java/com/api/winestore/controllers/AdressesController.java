package com.api.winestore.controllers;

import com.api.winestore.others.ResponseReturn;
import com.api.winestore.services.AddressesService;
import com.api.winestore.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/addresses")
public class AdressesController {

    private final AddressesService addressesService;
    private final UsersService usersService;




    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getByUserId(
            @RequestParam(value = "userId") UUID userId
    ) {
        var userOptional = usersService.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseReturn("Usuário não encontrado", null));
        }

        return ResponseEntity.ok(new ResponseReturn(null, userOptional.get().getAddresses()));
    }

}
