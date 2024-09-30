package com.api.winestore.services;

import com.api.winestore.entities.OrderProductEntity;
import com.api.winestore.repositories.OrderProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProductsService {

    private final OrderProductsRepository orderProductsRepository;




    public OrderProductEntity save(OrderProductEntity orderProduct) {
        return orderProductsRepository.save(orderProduct);
    }

}
