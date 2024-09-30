package com.api.winestore.entities;

import com.api.winestore.enums.CountryEnum;
import com.api.winestore.enums.ProductStatusEnum;
import com.api.winestore.enums.WineClassificationEnum;
import com.api.winestore.enums.WineTypeEnum;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "wines")
public class WineEntity implements PromotionalProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String description;

    @Enumerated(value = EnumType.STRING)
    private WineTypeEnum type;

    @Enumerated(value = EnumType.STRING)
    private CountryEnum country;

    @Enumerated(value = EnumType.STRING)
    private WineClassificationEnum classification;

    private String size;

    private Integer quantity;

    @Transient
    private Double currentPrice;

    private Double regPrice;

    private Boolean hasProm;

    private Integer promPercentage;

    private Double promPrice;

    private String[] images;

    @Enumerated(value = EnumType.STRING)
    private ProductStatusEnum status;

    @ManyToMany
    @JoinTable(
            name = "products_grapes",
            joinColumns = {@JoinColumn(name = "product_id")},
            inverseJoinColumns = {@JoinColumn(name = "grape_id")}
    )
    private Set<GrapeEntity> grapes;

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST})
    private List<OrderProductEntity> orderProducts;




    @Override
    @JsonGetter(value = "currentPrice")
    public double getCurrentPrice() {
        if (hasProm) return promPrice;
        return regPrice;
    }

}
