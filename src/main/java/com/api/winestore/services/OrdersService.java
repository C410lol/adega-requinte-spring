package com.api.winestore.services;

import com.api.winestore.entities.OrderEntity;
import com.api.winestore.repositories.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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


    public Page<OrderEntity> findAll(@NotNull String text, Pageable pageable) {
        if (text.isBlank()) text = "%";
        return ordersRepository.findAllByText(text, pageable);
    }

    public Optional<OrderEntity> findById(UUID orderId) {
        return ordersRepository.findById(orderId);
    }


}
