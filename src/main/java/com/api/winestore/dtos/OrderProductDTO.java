package com.api.winestore.dtos;

import java.util.UUID;

public record OrderProductDTO(
        UUID productId,
        double totalPrice,
        int quantity
) {
}
