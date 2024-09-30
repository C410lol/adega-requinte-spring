package com.api.winestore.controllers;

import com.api.winestore.dtos.GrapeDTO;
import com.api.winestore.dtos.WineDTO;
import com.api.winestore.entities.GrapeEntity;
import com.api.winestore.entities.WineEntity;
import com.api.winestore.enums.ProductStatusEnum;
import com.api.winestore.others.ResponseReturn;
import com.api.winestore.services.GrapesService;
import com.api.winestore.services.WinesService;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Array2DHashSet;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wines")
public class WinesController {

    private final WinesService winesService;
    private final GrapesService grapesService;




    @PostMapping("/save")
    public ResponseEntity<?> save(
            @RequestBody @NotNull WineDTO wineDTO
    ) {
        var wineEntity = new WineEntity();

        if (wineDTO.grapes().length < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseReturn("É necessário selecionar ao menos uma uva", null));
        }

        Set<GrapeEntity> grapes = new HashSet<>();
        for (GrapeDTO grapeDTO:
                wineDTO.grapes()) {
            if (grapeDTO.id() != null) {
                var grapeOptional = grapesService.findById(grapeDTO.id());
                if (grapeOptional.isPresent()) {
                    grapes.add(grapeOptional.get());
                    continue;
                }
            }

            var grapeOptionalName = grapesService.findByName(grapeDTO.name());
            if (grapeOptionalName.isPresent()) {
                grapes.add(grapeOptionalName.get());
                continue;
            }

            if (grapeDTO.name().isBlank()) continue;

            var grapeEntity = new GrapeEntity();
            grapeEntity.setName(grapeDTO.name());
            grapes.add(grapesService.save(grapeEntity));
        }

        BeanUtils.copyProperties(wineDTO, wineEntity);
        wineEntity.setGrapes(grapes);

        if (wineDTO.hasProm()) {
            wineEntity.setPromPercentage(
                    (int) ((wineDTO.regPrice() - wineDTO.promPrice()) * 100 / wineDTO.regPrice())
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseReturn(
                        "Produto criado com sucesso",
                        winesService.save(wineEntity)
                ));
    }


    // ------------------------------------------------------------------ //


    @GetMapping("/all")
    public ResponseEntity<?> getAll(
            @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc", required = false) String direction,
            @RequestParam(value = "pageNum", defaultValue = "0", required = false) int pageNum
    ) {
        var pageable = PageRequest.of(pageNum, 20, Sort.Direction.fromString(direction), sortBy);
        return ResponseEntity.ok(winesService.findAll(pageable));
    }

    @GetMapping("/{wineId}")
    public ResponseEntity<?> getById(
            @PathVariable(value = "wineId") UUID wineId
    ) {
        var wineOptional = winesService.findById(wineId);
        if (wineOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vinho não encontrado");
        }
        return ResponseEntity.ok(wineOptional.get());
    }


    // ------------------------------------------------------------------ //


    @PutMapping("/{productId}")
    public ResponseEntity<?> edit(
            @PathVariable(value = "productId") UUID productId,
            @RequestBody WineDTO wineDTO
    ) {
        var productOptional = winesService.findById(productId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseReturn(
                            String.format("Produto (ID -> %s) não encontrado", productId),
                            null
                    ));
        }

        if (wineDTO.grapes().length < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseReturn("É necessário selecionar ao menos uma uva", null));
        }

        Set<GrapeEntity> grapes = new HashSet<>();
        for (GrapeDTO grapeDTO:
                wineDTO.grapes()) {
            if (grapeDTO.id() != null) {
                var grapeOptional = grapesService.findById(grapeDTO.id());
                if (grapeOptional.isPresent()) {
                    grapes.add(grapeOptional.get());
                    continue;
                }
            }

            var grapeOptionalName = grapesService.findByName(grapeDTO.name());
            if (grapeOptionalName.isPresent()) {
                grapes.add(grapeOptionalName.get());
                continue;
            }

            if (grapeDTO.name().isBlank()) continue;

            var grapeEntity = new GrapeEntity();
            grapeEntity.setName(grapeDTO.name());
            grapes.add(grapesService.save(grapeEntity));
        }

        BeanUtils.copyProperties(wineDTO, productOptional.get());
        productOptional.get().setGrapes(grapes);

        if (wineDTO.hasProm()) {
            productOptional.get().setPromPercentage(
                    (int) ((wineDTO.regPrice() - wineDTO.promPrice()) * 100 / wineDTO.regPrice())
            );
        }

        return ResponseEntity.ok(
                new ResponseReturn("Produto editado com sucesos", winesService.save(productOptional.get()))
        );
    }


    // ------------------------------------------------------------------ //


    @DeleteMapping("/{productId}")
    public ResponseEntity<?> delete(
            @PathVariable(value = "productId") UUID productId
    ) {
        var productOptional = winesService.findById(productId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseReturn(
                            String.format("Produto (ID -> %s) não encontrado", productId),
                            null
                    ));
        }

        productOptional.get().setStatus(ProductStatusEnum.DELETED);
        winesService.save(productOptional.get());
        return ResponseEntity.ok(new ResponseReturn("Produto deletado com sucesso", null));
    }

}
