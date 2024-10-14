package com.api.winestore.services;

import com.api.winestore.entities.ProductEntity;
import com.api.winestore.repositories.ProductsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductsService {

    private final ProductsRepository productsRepository;

    @PersistenceContext
    private final EntityManager entityManager;




    public ProductEntity save(ProductEntity productEntity) {
        return productsRepository.save(productEntity);
    }


    // ------------------------------------------------------------------ //


    public Page<ProductEntity> findAll(Pageable pageable) {
        return productsRepository.findAllExcludeDeleted(pageable);
    }




    public Page<ProductEntity> findAllByFilters(
            String name,
            List<String> types,
            List<String> categories,
            List<String> countries,
            List<String> classifications,
            String orderBy,
            String direction,
            int size
    ) {
        var sqlString = new StringBuilder("SELECT p FROM ProductEntity p WHERE p.status != 'DELETED' ");

        if (name.isBlank()) name = "%";
        sqlString.append(String.format("AND UPPER(p.name) LIKE UPPER(concat('%%', '%s', '%%')) ", name));

        List<String> wineFilters = new ArrayList<>();
        if (!categories.isEmpty()) {
            wineFilters.add(String.format("p.category IN (%s)", getListOfFilter(categories)));
        }
        if (!countries.isEmpty()) {
            wineFilters.add(String.format("p.country IN (%s)", getListOfFilter(countries)));
        }
        if (!classifications.isEmpty()) {
            wineFilters.add(String.format("p.classification IN (%s)", getListOfFilter(classifications)));
        }

        joinFilters(sqlString, types, wineFilters);

        sqlString.append(String.format("ORDER BY status asc, p.%s %s", orderBy, direction));

        TypedQuery<ProductEntity> query = entityManager.createQuery(sqlString.toString(), ProductEntity.class);

        List<ProductEntity> wines = query.getResultList();

        return new PageImpl<>(
                wines,
                PageRequest.of(0, size, Sort.Direction.fromString(direction), orderBy),
                wines.size()
        );
    }

    private @NotNull String getListOfFilter(@NotNull List<String> elements) {
        StringBuilder list = new StringBuilder();

        for (String element:
                elements) {
            list.append(String.format("'%s',", element));
        }

        list.deleteCharAt(list.length() - 1);

        return list.toString();
    }

    private void joinFilters(
            StringBuilder sqlString,
            @NotNull List<String> types,
            List<String> wineFilters) {
        if (types.isEmpty() && wineFilters.isEmpty()) return;

        StringBuilder filterBuilder = new StringBuilder("AND (");

        if (!types.isEmpty()) {
            filterBuilder.append(String.format("p.type IN (%s) ", getListOfFilter(types)));
            if (!wineFilters.isEmpty()) filterBuilder.append("AND ");
        }
        if (!wineFilters.isEmpty()) filterBuilder.append(joinWineFilters(wineFilters));

        filterBuilder.append(") ");
        sqlString.append(filterBuilder);
    }

    private @NotNull String joinWineFilters(@NotNull List<String> wineFilters) {
        StringBuilder wineFiltersBuilder = new StringBuilder("(");
        wineFilters.forEach((e) -> wineFiltersBuilder.append(String.format("%s AND ", e)));
        wineFiltersBuilder.delete(wineFiltersBuilder.length() - 4, wineFiltersBuilder.length());
        wineFiltersBuilder.append(")");
        return wineFiltersBuilder.toString();
    }




    public Page<ProductEntity> findAllByText(@NotNull String text, Pageable pageable) {
        if (text.isBlank()) text = "%";
        return productsRepository.findAllByText(text, pageable);
    }

    public Optional<ProductEntity> findById(UUID id) {
        return productsRepository.findById(id);
    }


    // ------------------------------------------------------------------ //


    public void deleteById(UUID productId) {
        productsRepository.deleteById(productId);
    }

}
