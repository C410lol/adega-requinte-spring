package com.api.winestore.others;

import java.util.UUID;

public record LoginReturnDTO(
        UUID userId,
        String token
) {
}
