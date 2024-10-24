package com.api.winestore.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "countries")
public class CountryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "image_url")
    private String imageURL;

    private String name;



    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "country", cascade = {CascadeType.PERSIST})
    private List<ProductEntity> products;

}
