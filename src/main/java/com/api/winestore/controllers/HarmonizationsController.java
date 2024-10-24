package com.api.winestore.controllers;

import com.api.winestore.others.ResponseReturn;
import com.api.winestore.services.HarmonizationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/harmonizations")
public class HarmonizationsController {

    private final HarmonizationsService harmonizationsService;




    @GetMapping("/all-by-name")
    public ResponseEntity<?> findByName(
            @RequestParam(value = "name", defaultValue = "%") String name
    ) {
        return ResponseEntity.ok(new ResponseReturn(
                null,
                harmonizationsService.findAllByName(name)
        ));
    }

}
