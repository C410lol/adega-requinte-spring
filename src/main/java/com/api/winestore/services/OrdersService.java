package com.api.winestore.services;

import com.api.winestore.entities.OrderEntity;
import com.api.winestore.repositories.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;




    public OrderEntity save(OrderEntity order) {
        return ordersRepository.save(order);
    }


    // ------------------------------------------------------------------ //


    public Page<OrderEntity> findAll(Pageable pageable) {
        return ordersRepository.findAll(pageable);
    }

    public Optional<OrderEntity> findById(UUID orderId) {
        return ordersRepository.findById(orderId);
    }


}
