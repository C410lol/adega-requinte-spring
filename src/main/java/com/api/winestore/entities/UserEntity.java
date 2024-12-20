package com.api.winestore.entities;

import com.api.winestore.enums.RoleEnum;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String email;

    private String phone;

    private String password;

    private boolean isMember;

    @Enumerated(value = EnumType.STRING)
    private RoleEnum role;

    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<OrderEntity> orders;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<AddressEntity> addresses;




    @JsonGetter(value = "orders")
    public int getOrdersSize() {
        if (orders != null) return orders.size();
        return 0;
    }

}
