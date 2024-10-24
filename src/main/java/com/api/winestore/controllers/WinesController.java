package com.api.winestore.controllers;

import com.api.winestore.dtos.GrapeDTO;
import com.api.winestore.dtos.HarmonizationDTO;
import com.api.winestore.dtos.ImgurApiResponse;
import com.api.winestore.dtos.ProductDTO;
import com.api.winestore.entities.GrapeEntity;
import com.api.winestore.entities.HarmonizationEntity;
import com.api.winestore.entities.ProductEntity;
import com.api.winestore.enums.ProductStatusEnum;
import com.api.winestore.enums.ProductTypeEnum;
import com.api.winestore.others.ResponseReturn;
import com.api.winestore.services.CountriesService;
import com.api.winestore.services.GrapesService;
import com.api.winestore.services.HarmonizationsService;
import com.api.winestore.services.ProductsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wines")
public class WinesController {

    private final ProductsService productsService;
    private final GrapesService grapesService;
    private final HarmonizationsService harmonizationsService;
    private final CountriesService countriesService;




    @PostMapping("/save")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> save(
            @RequestPart(name = "images", required = false) List<MultipartFile> images,
            @RequestParam(name = "product") String productDTORaw
    ) throws IOException, URISyntaxException, InterruptedException {
        var productEntity = new ProductEntity();
        var productDTO = new ObjectMapper().readValue(productDTORaw, ProductDTO.class);
        BeanUtils.copyProperties(productDTO, productEntity);

        if (productDTO.hasProm()) {
            productEntity.setPromPercentage(
                    (int) ((productDTO.regPrice() - productDTO.promPrice()) * 100 / productDTO.regPrice())
            );
        }

        if (images != null) {
            List<String> imagesList = new ArrayList<>();
            var httpClient = HttpClient.newHttpClient();
            for (MultipartFile image:
                    images) {
                var httpRequest = HttpRequest.newBuilder()
                        .uri(new URI("https://api.imgur.com/3/image"))
                        .header("Authorization", "Client-ID c3a808df575f280")
                        .POST(HttpRequest.BodyPublishers.ofByteArray(image.getBytes()))
                        .build();

                var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                var imgurApiResponse = new ObjectMapper().readValue(httpResponse.body(), ImgurApiResponse.class);

                if (!imgurApiResponse.success()) continue;

                imagesList.add(imgurApiResponse.data().link());
            }

            productEntity.setImages(imagesList);
        }


        if (
                productDTO.type().equals(ProductTypeEnum.Vinho) ||
                productDTO.type().equals(ProductTypeEnum.Suco)
        ) {
            Set<GrapeEntity> grapes = new HashSet<>();
            for (GrapeDTO grapeDTO:
                    productDTO.grapes()) {
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
            productEntity.setGrapes(grapes);

            Set<HarmonizationEntity> harmonizations = new HashSet<>();
            for (HarmonizationDTO harmonizationDTO:
                    productDTO.harmonizations()) {
                if (harmonizationDTO.id() != null) {
                    var harmonizationOptional = harmonizationsService.findById(harmonizationDTO.id());
                    if (harmonizationOptional.isPresent()) {
                        harmonizations.add(harmonizationOptional.get());
                        continue;
                    }
                }

                var harmonizationOptionalName = harmonizationsService.findByName(harmonizationDTO.name());
                if (harmonizationOptionalName.isPresent()) {
                    harmonizations.add(harmonizationOptionalName.get());
                    continue;
                }

                if (harmonizationDTO.name().isBlank()) continue;

                var harmonizationEntity = new HarmonizationEntity();
                harmonizationEntity.setName(harmonizationDTO.name());
                harmonizations.add(harmonizationsService.save(harmonizationEntity));
            }
            productEntity.setHarmonizationTags(harmonizations);

            var countryOptional = countriesService.findById(productDTO.countryId());
            if (countryOptional.isPresent()) productEntity.setCountry(countryOptional.get());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseReturn(
                        "Produto criado com sucesso",
                        productsService.save(productEntity)
                ));
    }


    // ------------------------------------------------------------------ //


    @GetMapping("/all")
    public ResponseEntity<?> getAll(
            @RequestParam(value = "name", defaultValue = "%", required = false) String name,
            @RequestParam(value = "types", defaultValue = "", required = false) List<String> types,
            @RequestParam(value = "categories", defaultValue = "", required = false) List<String> categories,
            @RequestParam(value = "countries", defaultValue = "", required = false) List<String> countries,
            @RequestParam(value = "classifications", defaultValue = "", required = false) List<String> classifications,
            @RequestParam(value = "orderBy", defaultValue = "name", required = false) String orderBy,
            @RequestParam(value = "direction", defaultValue = "asc", required = false) String direction
    ) {
        return ResponseEntity.ok(new ResponseReturn(
                null,
                productsService.findAllByFilters(
                        name,
                        types,
                        categories,
                        countries,
                        classifications,
                        orderBy,
                        direction,
                        Integer.MAX_VALUE
                )
        ));
    }

    @GetMapping("/all-by-text")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllByText(
            @RequestParam(value = "text", defaultValue = "%", required = false) String text
    ) {
        return ResponseEntity.ok(new ResponseReturn(
                null,
                productsService.findAllByText(text)
        ));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getById(
            @PathVariable(value = "productId") UUID productId
    ) {
        var productOptional = productsService.findById(productId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vinho não encontrado");
        }
        return ResponseEntity.ok(productOptional.get());
    }


    // ------------------------------------------------------------------ //


    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> edit(
            @PathVariable(value = "productId") UUID productId,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "product") String productDTORaw
    ) throws IOException, URISyntaxException, InterruptedException {
        var productOptional = productsService.findById(productId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseReturn(
                            String.format("Produto (ID -> %s) não encontrado", productId),
                            null
                    ));
        }

        var productDTO = new ObjectMapper().readValue(productDTORaw, ProductDTO.class);
        BeanUtils.copyProperties(productDTO, productOptional.get());
        if (productDTO.hasProm()) {
            productOptional.get().setPromPercentage(
                    (int) ((productDTO.regPrice() - productDTO.promPrice()) * 100 / productDTO.regPrice())
            );
        }




        if (images != null) {
            List<String> imagesList = new ArrayList<>(productDTO.images());

            var httpClient = HttpClient.newHttpClient();
            for (MultipartFile image:
                    images) {
                var httpRequest = HttpRequest.newBuilder()
                        .uri(new URI("https://api.imgur.com/3/image"))
                        .header("Authorization", "Client-ID c3a808df575f280")
                        .POST(HttpRequest.BodyPublishers.ofByteArray(image.getBytes()))
                        .build();

                var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                var imgurApiResponse = new ObjectMapper().readValue(httpResponse.body(), ImgurApiResponse.class);

                if (!imgurApiResponse.success()) continue;

                imagesList.add(imgurApiResponse.data().link());
            }

            productOptional.get().setImages(imagesList);
        }




        if (
                productDTO.type().equals(ProductTypeEnum.Vinho) ||
                productDTO.type().equals(ProductTypeEnum.Suco)
        ) {
            Set<GrapeEntity> grapes = new HashSet<>();
            for (GrapeDTO grapeDTO:
                    productDTO.grapes()) {
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
            productOptional.get().setGrapes(grapes);

            Set<HarmonizationEntity> harmonizations = new HashSet<>();
            for (HarmonizationDTO harmonizationDTO:
                    productDTO.harmonizations()) {
                if (harmonizationDTO.id() != null) {
                    var harmonizationOptional = harmonizationsService.findById(harmonizationDTO.id());
                    if (harmonizationOptional.isPresent()) {
                        harmonizations.add(harmonizationOptional.get());
                        continue;
                    }
                }

                var harmonizationOptionalName = harmonizationsService.findByName(harmonizationDTO.name());
                if (harmonizationOptionalName.isPresent()) {
                    harmonizations.add(harmonizationOptionalName.get());
                    continue;
                }

                if (harmonizationDTO.name().isBlank()) continue;

                var harmonizationEntity = new HarmonizationEntity();
                harmonizationEntity.setName(harmonizationDTO.name());
                harmonizations.add(harmonizationsService.save(harmonizationEntity));
            }
            productOptional.get().setHarmonizationTags(harmonizations);

            var countryOptional = countriesService.findById(productDTO.countryId());
            if (countryOptional.isPresent()) productOptional.get().setCountry(countryOptional.get());
        }




        return ResponseEntity.ok(
                new ResponseReturn("Produto editado com sucesos", productsService.save(productOptional.get()))
        );
    }


    // ------------------------------------------------------------------ //


    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> delete(
            @PathVariable(value = "productId") UUID productId
    ) {
        var productOptional = productsService.findById(productId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseReturn(
                            String.format("Produto (ID -> %s) não encontrado", productId),
                            null
                    ));
        }

        productOptional.get().setStatus(ProductStatusEnum.DELETED);
        productsService.save(productOptional.get());
        return ResponseEntity.ok(new ResponseReturn("Produto deletado com sucesso", null));
    }

}
