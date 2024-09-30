package com.api.winestore.dtos;

public record AddressDTO(
        String name,
        String cep,
        String street,
        String number,
        String complement,
        String referencePoint,
        String neighborhood,
        String city,
        String state
) {
}
