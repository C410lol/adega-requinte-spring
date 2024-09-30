package com.api.winestore.dtos;

import com.api.winestore.enums.DeliveryEnum;
import com.api.winestore.enums.PaymentEnum;

import java.util.List;

public record OrderDTO(
        DeliveryEnum delivery,
        AddressDTO address,
        PaymentEnum payment,
        double exchange,
        List<OrderProductDTO> orderProducts
) {
}
