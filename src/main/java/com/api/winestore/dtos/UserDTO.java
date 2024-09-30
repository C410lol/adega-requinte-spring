package com.api.winestore.dtos;

public record UserDTO(
        String name,
        String email,
        String phone,
        String password
) {
}
