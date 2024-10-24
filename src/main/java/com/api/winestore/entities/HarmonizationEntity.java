package com.api.winestore.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "harmonizations")
public class HarmonizationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;




    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(mappedBy = "harmonizationTags")
    private List<ProductEntity> products;

}
