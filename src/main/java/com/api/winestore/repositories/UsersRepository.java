package com.api.winestore.repositories;

import com.api.winestore.entities.UserEntity;
import com.api.winestore.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<UserEntity, UUID> {

    @Query(
            value = "SELECT * FROM users " +
                    "WHERE role = :role AND (" +
                    "UPPER(name) LIKE UPPER(CONCAT('%', :text, '%')) OR " +
                    "UPPER(email) LIKE UPPER(CONCAT('%', :text, '%')) OR " +
                    "UPPER(phone) LIKE UPPER(CONCAT('%', :text, '%')))",
            nativeQuery = true
    )
    List<UserEntity> findAllByRoleAndText(
            @Param(value = "role") String role,
            @Param(value = "text") String text
    );

    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);

}
