package com.api.winestore.controllers;

import com.api.winestore.dtos.OrderDTO;
import com.api.winestore.dtos.OrderProductDTO;
import com.api.winestore.entities.AddressEntity;
import com.api.winestore.entities.OrderEntity;
import com.api.winestore.entities.OrderProductEntity;
import com.api.winestore.enums.DeliveryEnum;
import com.api.winestore.enums.OrderStatusEnum;
import com.api.winestore.enums.ProductStatusEnum;
import com.api.winestore.others.ResponseReturn;
import com.api.winestore.services.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrdersController {

    private final OrdersService ordersService;
    private final UsersService usersService;
    private final WinesService winesService;
    private final AddressesService addressesService;
    private final OrderProductsService orderProductsService;




    @PostMapping("/save")
    public ResponseEntity<?> saveOrder(
            @RequestParam(value = "userId") UUID userId,
            @RequestParam(value = "addressId", required = false) UUID addressId,

            @RequestBody @NotNull OrderDTO orderDTO
    ) {
        var orderEntity = new OrderEntity();
        orderEntity.setDate(LocalDate.now(ZoneId.of("UTC-3")));
        orderEntity.setDelivery(orderDTO.delivery());
        orderEntity.setPayment(orderDTO.payment());
        orderEntity.setExchange(orderDTO.exchange());
        orderEntity.setStatus(OrderStatusEnum.CONFIRMANDO);

        //SET USER
        var userOptional = usersService.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseReturn(
                            "Usuário não encontrado",
                            null
                    ));
        }
        orderEntity.setUser(userOptional.get());
        //SET USER

        //SET ADDRESS
        if (orderDTO.delivery().equals(DeliveryEnum.ENTREGAR)) {
            if (addressId != null) {
                var addressOptional = addressesService.findById(addressId);
                if (addressOptional.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ResponseReturn(
                                    String.format("Endereço (ID -> %s) não encontrado", addressId),
                                    null
                            ));
                }

                orderEntity.setAddress(addressOptional.get());
            } else {
                var addressEntity = new AddressEntity();
                BeanUtils.copyProperties(orderDTO.address(), addressEntity);
                addressEntity.setUser(userOptional.get());

                orderEntity.setAddress(addressesService.save(addressEntity));
            }
        }
        //SET ADDRESS

        //SET TOTAL PRICE && CHECK PRODUCT QUANTITY
        double totalPrice = 0;
        for (OrderProductDTO orderProductDTO:
                orderDTO.orderProducts()) {
            var productOptional = winesService.findById(orderProductDTO.productId());
            if (productOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseReturn(
                                String.format("Produto (ID -> %s) não encontrado", orderProductDTO.productId()),
                                null
                        ));
            }

            if (productOptional.get().getStatus().equals(ProductStatusEnum.INDISPONÍVEL)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseReturn(
                                String.format("Produto (ID -> %s) indisponível", productOptional.get().getId()),
                                null
                        ));
            }

            int discountedStock = productOptional.get().getQuantity() - orderProductDTO.quantity();

            if (discountedStock < 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseReturn(
                                String.format("Produto (ID -> %s) tem estoque insulficiente", productOptional.get().getId()),
                                null
                        ));
            }

            //SET NEW PRODUCT QUANTITY
            productOptional.get().setQuantity(discountedStock);

            //CHECK IF THE STOCK IS OVER
            if (discountedStock == 0) {
                productOptional.get().setStatus(ProductStatusEnum.INDISPONÍVEL);
            }

            winesService.save(productOptional.get());

            totalPrice += productOptional.get().getCurrentPrice() * orderProductDTO.quantity();
        }

        if (orderDTO.delivery() == DeliveryEnum.ENTREGAR) totalPrice+= 5;

        var decimalFormat = new DecimalFormat("#.##");
        orderEntity.setTotalPrice(Double.parseDouble(decimalFormat.format(totalPrice)));
        //SET TOTAL PRICE && CHECK PRODUCT QUANTITY

        var createdOrderEntity = ordersService.save(orderEntity);

        //PERSIST OrderProduct Entities IN DATABASE
        for (OrderProductDTO orderProductDTO:
                orderDTO.orderProducts()) {
            var productOptional = winesService.findById(orderProductDTO.productId());

            var orderProductEntity = new OrderProductEntity();
            orderProductEntity.setProduct(productOptional.get());
            orderProductEntity.setTotalPrice(Double.parseDouble(decimalFormat.format(orderProductDTO.totalPrice())));
            orderProductEntity.setQuantity(orderProductDTO.quantity());
            orderProductEntity.setOrder(createdOrderEntity);

            orderProductsService.save(orderProductEntity);
        }
        //PERSIST OrderProduct Entities IN DATABASE

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseReturn(
                        "Pedido realizado com sucesso",
                        createdOrderEntity
                ));
    }


    // ------------------------------------------------------------------ //


    @GetMapping("/all")
    public ResponseEntity<?> getAll(
            @RequestParam(value = "sortBy", defaultValue = "orderNumber", required = false) String sortBy,
            @RequestParam(value = "direction", defaultValue = "desc", required = false) String direction,
            @RequestParam(value = "pageNum", defaultValue = "0", required = false) int pageNum
    ) {
        var pageable = PageRequest.of(pageNum, 20, Sort.Direction.fromString(direction), sortBy);
        return ResponseEntity.ok(new ResponseReturn(null, ordersService.findAll(pageable)));
    }

    @GetMapping("/all-by-user")
    public ResponseEntity<?> getAllByUserId(
            @RequestParam(value = "userId") UUID userId
    ) {
        var userOptional = usersService.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseReturn("Usuário não encontrado", null));
        }

        userOptional.get().getOrders().sort(Comparator.comparing(OrderEntity::getOrderNumber).reversed());

        return ResponseEntity.ok(new ResponseReturn(null, userOptional.get().getOrders()));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getById(
            @PathVariable(value = "orderId") UUID orderId
    ) {
        var orderOptional = ordersService.findById(orderId);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseReturn("Pedido não encontrado", null));
        }

        return ResponseEntity.ok(
                new ResponseReturn(null, orderOptional.get())
        );
    }


    // ------------------------------------------------------------------ //


    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(
            @PathVariable(value = "orderId") UUID orderId
    ) {
        var orderOptional = ordersService.findById(orderId);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseReturn("Pedido não encontrado", null));
        }

        orderOptional.get().setStatus(OrderStatusEnum.CANCELADO);
        ordersService.save(orderOptional.get());
        return ResponseEntity.ok(new ResponseReturn("Pedido cancelado com sucesso", null));
    }

}
