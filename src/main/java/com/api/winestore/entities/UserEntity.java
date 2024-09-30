package com.api.winestore.entities;

import com.api.winestore.enums.RoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

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

    @Enumerated(value = EnumType.STRING)
    private RoleEnum role;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<OrderEntity> orders;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<AddressEntity> addresses;

}
