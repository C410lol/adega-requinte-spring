package com.api.winestore.dtos;

import com.api.winestore.enums.*;

import java.util.List;
import java.util.UUID;

public record ProductDTO(
        String name,
        String description,

        ProductTypeEnum type,
        WineCategoryEnum category,
        UUID countryId,
        WineClassificationEnum classification,
        HarmonizationDTO[] harmonizations,
        GrapeDTO[] grapes,

        String size,

        int quantity,
        double regPrice,
        boolean hasProm,
        double promPrice,
        List<String> images,
        ProductStatusEnum status
) { }
