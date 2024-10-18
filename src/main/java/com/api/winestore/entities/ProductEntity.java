package com.api.winestore.entities;

import com.api.winestore.enums.*;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String description;

    @Enumerated(value = EnumType.STRING)
    private ProductTypeEnum type;

    @Enumerated(value = EnumType.STRING)
    private WineCategoryEnum category;

    @Enumerated(value = EnumType.STRING)
    private CountryEnum country;

    @Enumerated(value = EnumType.STRING)
    private WineClassificationEnum classification;

    private String size;

    private Integer quantity;

    @Transient
    private Double currentPrice;

    private Double regPrice;

    @Transient
    private Double memberPrice;

    private Boolean hasProm;

    private Integer promPercentage;

    private Double promPrice;

    private List<String> images;

    @Enumerated(value = EnumType.STRING)
    private ProductStatusEnum status;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "products_grapes",
            joinColumns = {@JoinColumn(name = "product_id")},
            inverseJoinColumns = {@JoinColumn(name = "grape_id")}
    )
    private Set<GrapeEntity> grapes;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST})
    private List<OrderProductEntity> orderProducts;




    @JsonGetter(value = "category")
    public String getCategory() {
        if (category == null) return null;
        return category.name().replaceAll("_", " ");
    }

    @JsonGetter(value = "classification")
    public String getClassification() {
        if (classification == null) return null;
        return classification.name().replaceAll("_", " ");
    }

    @JsonGetter(value = "currentPrice")
    public double getCurrentPrice() {
        if (hasProm) return promPrice;
        return regPrice;
    }

    @JsonGetter(value = "memberPrice")
    public double getMemberPrice() {
        return Double.parseDouble(new DecimalFormat("#.##").format(((regPrice * 90) / 100)));
    }

}
