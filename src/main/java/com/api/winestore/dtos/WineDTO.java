package com.api.winestore.dtos;

import com.api.winestore.enums.CountryEnum;
import com.api.winestore.enums.ProductStatusEnum;
import com.api.winestore.enums.WineClassificationEnum;
import com.api.winestore.enums.WineTypeEnum;

public record WineDTO(
        String name,
        String description,
        WineTypeEnum type,
        CountryEnum country,
        WineClassificationEnum classification,
        GrapeDTO[] grapes,
        String size,
        int quantity,
        double regPrice,
        boolean hasProm,
        double promPrice,
        String[] images,
        ProductStatusEnum status
) {
}
