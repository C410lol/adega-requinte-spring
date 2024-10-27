package com.api.winestore.entities;

import com.api.winestore.enums.DeliveryEnum;
import com.api.winestore.enums.OrderStatusEnum;
import com.api.winestore.enums.PaymentEnum;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_number", unique = true, insertable = false, nullable = false, updatable = false)
    private Long orderNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private LocalDate date;

    @Enumerated(value = EnumType.STRING)
    private DeliveryEnum delivery;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    @Enumerated(value = EnumType.STRING)
    private PaymentEnum payment;

    private Double exchange;

    private boolean hasMemberDiscount;

    private Double totalPrice;

    @Transient
    private Integer totalProducts;

    @Enumerated(value = EnumType.STRING)
    private OrderStatusEnum status;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.ALL})
    private List<OrderProductEntity> orderProducts;




    @JsonGetter(value = "date")
    public String getFormatedDate() {
        var dateSplited = date.toString().split("-");
        return String.format("%s/%s/%s", dateSplited[2], dateSplited[1], dateSplited[0]);
    }

    @JsonGetter(value = "delivery")
    public String getFormatedDelivery() {
        return delivery.name().charAt(0) + delivery.name().substring(1).toLowerCase();
    }

    @JsonGetter(value = "payment")
    public String getFormatedPayment() {
        return payment.name().charAt(0) + payment.name().substring(1).toLowerCase();
    }

    @JsonGetter(value = "totalProducts")
    public Integer getTotalProducts() {
        if (orderProducts != null) return orderProducts.size();
        return 0;
    }

}
