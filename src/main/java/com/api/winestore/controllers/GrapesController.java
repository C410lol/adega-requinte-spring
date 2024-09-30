package com.api.winestore.controllers;

import com.api.winestore.others.ResponseReturn;
import com.api.winestore.services.GrapesService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/grapes")
public class GrapesController {

    private final GrapesService grapesService;




    @GetMapping("/all-by-name")
    public ResponseEntity<?> findAllByName(
            @RequestParam(value = "name") @NotNull String name
    ) {
        if (name.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseReturn("O campo 'name' não pode estar vazio", null));
        }

        return ResponseEntity.ok(new ResponseReturn(null, grapesService.findAllByName(name)));
    }

}
