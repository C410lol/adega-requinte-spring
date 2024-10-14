package com.api.winestore.dtos;

import com.api.winestore.enums.*;

import java.util.List;

public record ProductDTO(
        String name,
        String description,
        ProductTypeEnum type,
        WineCategoryEnum category,
        CountryEnum country,
        WineClassificationEnum classification,
        GrapeDTO[] grapes,
        String size,
        int quantity,
        double regPrice,
        boolean hasProm,
        double promPrice,
        List<String> images,
        ProductStatusEnum status
) {
}
